package com.pisces.framework.rds.provider;

import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.rds.helper.entity.EntityTable;
import com.pisces.framework.rds.helper.EntityHelper;
import com.pisces.framework.rds.helper.MapperHelper;
import com.pisces.framework.rds.helper.SqlHelper;
import com.pisces.framework.rds.utils.MetaObjectUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.type.JdbcType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 自定义提供程序
 *
 * @author jason
 * @date 2022/12/07
 */
public class RdsProvider extends BaseProvider {

    public RdsProvider(Class<?> mapperClazz, MapperHelper mapperHelper) {
        super(mapperClazz, mapperHelper);
    }

    private static boolean notChecked = true;

    public void checkTable(MappedStatement ms) throws SQLException {
        if (notChecked) {
            notChecked = false;
//            ms.getConfiguration().setObjectWrapperFactory(new EntityWrapperFactory());
//            ms.getConfiguration().setObjectFactory(new MybatisEntityFactory());
//            Transmit.instance.start();
            return;
        }
        Class<?> entityClass = getEntityClass(ms);
        try (Connection conn = ms.getConfiguration().getEnvironment().getDataSource().getConnection()) {
            if (!doExistedTable(conn, ms)) {
                doCreateTable(conn, ms);
                return;
            }

            Map<String, EntityColumn> existedColumns = getExistedColumns(conn, entityClass);
            List<EntityColumn> addColumns = new ArrayList<>();
            List<EntityColumn> changeColumns = new ArrayList<>();
            for (EntityColumn column : EntityHelper.getColumns(entityClass)) {
                EntityColumn existedColumn = existedColumns.get(column.getColumn());
                if (existedColumn != null) {
                    if (!(getProvider(ms).compatible(column.getJdbcType(), existedColumn.getJdbcType()))) {
                        changeColumns.add(column);
                    }
                    existedColumns.remove(column.getColumn());
                } else {
                    addColumns.add(column);
                }
            }

            if (!addColumns.isEmpty()) {
                autoAddColumns(conn, ms, addColumns);
            }

            if (!changeColumns.isEmpty()) {
                autoChangeColumns(conn, ms, changeColumns);
            }
        }
    }

    private boolean doExistedTable(Connection conn, MappedStatement ms) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        return getProvider(ms).existedTable(conn, conn.getCatalog(), tableName(entityClass));
    }

    private void doCreateTable(Connection conn, MappedStatement ms) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        EntityTable table = EntityHelper.getEntityTable(entityClass);
        this.getProvider(ms).createTable(conn, tableName(entityClass), table.getEntityClassColumns());
    }

    private Map<String, EntityColumn> getExistedColumns(Connection conn, Class<?> entityClass) throws SQLException {
        Map<String, EntityColumn> existedColumns = new HashMap<>(16);
        try (ResultSet resultSet = conn.getMetaData().getColumns(conn.getCatalog(), null, tableName(entityClass), null)) {
            while (resultSet.next()) {
                EntityColumn column = new EntityColumn();
                column.setColumn(resultSet.getString(4));
                column.setJdbcType(JdbcType.forCode(resultSet.getInt("DATA_TYPE")));
                existedColumns.put(column.getColumn(), column);
            }
        }
        return existedColumns;
    }

    private void autoAddColumns(Connection conn, MappedStatement ms, List<EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String addSQL = this.getProvider(ms).addColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(addSQL)) {
            stmt.execute();
        }
    }

    private void autoChangeColumns(Connection conn, MappedStatement ms, List<EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String changeSQL = this.getProvider(ms).changeColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(changeSQL)) {
            stmt.execute();
        }
    }

    private void autoDropColumns(Connection conn, MappedStatement ms, Map<String, EntityColumn> columns) throws SQLException {
        Class<?> entityClass = getEntityClass(ms);
        final String changeSQL = this.getProvider(ms).dropColumns(tableName(entityClass), columns);
        try (PreparedStatement stmt = conn.prepareStatement(changeSQL)) {
            stmt.execute();
        }
    }

    /**
     * 查询全部结果
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String selectAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));

        // 逻辑删除的未删除查询条件
        sql.append("<where>");
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 根据主键进行查询
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String selectById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        return SqlHelper.selectAllColumns(entityClass) +
                SqlHelper.fromTable(entityClass, tableName(entityClass)) +
                SqlHelper.wherePKColumns(entityClass);
    }

    public String selectByIds(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        setResultType(ms, entityClass);
        return SqlHelper.selectAllColumns(entityClass) +
                SqlHelper.fromTable(entityClass, tableName(entityClass)) +
                " WHERE id IN " +
                "<foreach item=\"item\" index=\"index\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">" +
                "#{item}" +
                "</foreach>";
    }

    /**
     * 根据主键查询总数
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String existsById(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return SqlHelper.selectCountExists(entityClass) +
                SqlHelper.fromTable(entityClass, tableName(entityClass)) +
                SqlHelper.wherePKColumns(entityClass);
    }

    public String insert(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (column.isIdentity()) {
                sql.append(column.getColumn()).append(",");
            } else {
                if (logicDeleteColumn != null && logicDeleteColumn == column) {
                    sql.append(column.getColumn()).append(",");
                    continue;
                }
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", isNotEmpty()));
            }
        }
        sql.append("</trim>");

        sql.append("<trim prefix=\"VALUES(\" suffix=\")\" suffixOverrides=\",\">");
        for (EntityColumn column : columnList) {
            if (!column.isInsertable()) {
                continue;
            }
            if (logicDeleteColumn != null && logicDeleteColumn == column) {
                sql.append(SqlHelper.getLogicDeletedValue(column, false)).append(",");
                continue;
            }
            //优先使用传入的属性值,当原属性property!=null时，用原属性
            //自增的情况下,如果默认有值,就会备份到property_cache中,所以这里需要先判断备份的值是否存在
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheNotNull(column, column.getColumnHolder(null, "_cache", ",")));
            } else {
                //其他情况值仍然存在原property中
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), isNotEmpty()));
            }
            //当属性为null时，如果存在主键策略，会自动获取值，如果不存在，则使用null
            //序列的情况
            if (column.isIdentity()) {
                sql.append(SqlHelper.getIfCacheIsNull(column, column.getColumnHolder() + ","));
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * 批量插入
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String insertBatch(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, '").append(ms.getId()).append(" 方法参数为空')\"/>");
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass), "list[0]"));
        sql.append(SqlHelper.insertColumns(entityClass, true, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isInsertable()) {
                sql.append(column.getColumnHolder("record")).append(",");
            }
        }
        sql.append("</trim>");
        sql.append("</foreach>");

        // 反射把MappedStatement中的设置主键名
//        EntityHelper.setKeyProperties(EntityHelper.getPKColumns(entityClass), ms);

        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String update(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        return SqlHelper.updateTable(entityClass, tableName(entityClass)) +
                SqlHelper.updateSetColumns(entityClass, null, true, isNotEmpty()) +
                SqlHelper.wherePKColumns(entityClass, true);
    }

    /**
     * 批量更新
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String updateBatch(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
//        String sql = "<foreach item=\"item\" index=\"index\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">" +
//                SqlHelper.updateTable(entityClass, tableName(entityClass)) +
//                SqlHelper.updateSetColumns(entityClass, null, true, isNotEmpty()) +
//                SqlHelper.wherePKColumns(entityClass, true) +
//                "</foreach>";
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append("<trim prefix=\"set\" suffixOverrides=\",\">");

        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {
                sql.append("  <trim prefix=\"").append(column.getColumn()).append(" =case\" suffix=\"end,\">");
                sql.append("    <foreach collection=\"list\" item=\"i\" index=\"index\">");
                sql.append("      <if test=\"i.").append(column.getEntityField().getName()).append("!=null\">");
                sql.append("         when id=#{i.id} then #{i.").append(column.getEntityField().getName()).append("}");
                sql.append("      </if>");
                sql.append("    </foreach>");
                sql.append("  </trim>");
            }
        }

        sql.append("</trim>");
        sql.append("WHERE");
        sql.append(" ID IN ");
        sql.append("<trim prefix=\"(\" suffix=\")\">");
        sql.append("<foreach collection=\"list\" separator=\", \" item=\"i\" index=\"index\" >");
        sql.append("#{i.id}");
        sql.append("</foreach>");
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * 通过条件删除
     *
     * @param ms 女士
     * @return {@link String}
     */
    public String deleteById(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
            sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
            sql.append("<set>");
            sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
            sql.append("</set>");
            MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
        } else {
            sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        }
        sql.append(SqlHelper.wherePKColumns(entityClass));
        return sql.toString();
    }

    public String deleteBatchByIds(MappedStatement ms) {
        StringBuilder sql = new StringBuilder();
        final Class<?> entityClazz = getEntityClass(ms);
        if (this.getConfig().isSafeDelete()) {
            sql.append(SqlHelper.notAllNullParameterCheck("_parameter", EntityHelper.getColumns(entityClazz)));
        }
        sql.append(SqlHelper.deleteFromTable(entityClazz, tableName(entityClazz)) +
                "WHERE ID IN " +
                "<foreach item=\"item\" index=\"index\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">" +
                "#{item}" +
                "</foreach>");
        return sql.toString();
    }

    public String deleteBatch(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        String sql = SqlHelper.deleteFromTable(entityClass, tableName(entityClass)) +
                " WHERE id IN " +
                "<foreach item=\"item\" index=\"index\" collection=\"list\" open=\"(\" separator=\",\" close=\")\">" +
                "#{item.id}" +
                "</foreach>";
        return sql;
    }
}
