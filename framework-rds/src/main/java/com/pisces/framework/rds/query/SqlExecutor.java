package com.pisces.framework.rds.query;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.entity.factory.FactoryManager;
import com.pisces.framework.core.entity.table.QBeanObject;
import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.helper.EntityHelper;
import com.pisces.framework.rds.helper.entity.EntityColumn;
import com.pisces.framework.rds.helper.entity.EntityTable;
import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql执行程序
 *
 * @author jason
 * @date 2023/07/09
 */
public class SqlExecutor {

    /**
     * 获取SqlSessionFactory
     */
    private static SqlSessionFactory getSqlSessionFactory() {
        return AppUtils.getBean(SqlSessionFactory.class);
    }

    /**
     * 获取Session 默认自动提交
     */
    private static SqlSession sqlSession() {
        return SqlSessionUtils.getSqlSession(getSqlSessionFactory());
    }

    /**
     * 释放sqlSession
     *
     * @param sqlSession session
     */
    private static void closeSqlSession(SqlSession sqlSession) {
        SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
    }

    private static void bindTenant(QueryWrapper qw) {
        long tenant = AppUtils.getTenant();
        if (tenant != AppUtils.ROOT_TENANT) {
            List<QueryTable> allTables = qw.getAllTables();
            for (QueryTable table : allTables) {
                qw.and(QBeanObject.tenant.bind(table.getBeanClass()).equal(tenant));
            }
        }
    }

    public static void bindEnabled(QueryWrapper qw) {
        List<QueryTable> allTables = qw.getAllTables();
        for (QueryTable table : allTables) {
            qw.and(QBeanObject.enabled.bind(table.getBeanClass()).equal(true));
        }
    }

    public static List<Map<String, Object>> fetchRaw(QueryWrapper qw) {
        List<Map<String, Object>> data = null;
        bindTenant(qw);
        final String sql = SqlTools.toSql(qw);
        final Object[] args = SqlParams.getValueArray(qw);
        SqlSession sqlSession = sqlSession();
        try (Connection connection = sqlSession.getConnection()) {
            SqlRunner sqlRunner = new PiscesSqlRunner(connection);
            data = sqlRunner.selectAll(sql, args);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            closeSqlSession(sqlSession);
        }
        return CollectionUtils.isEmpty(data) ? new ArrayList<>() : data;
    }

    public static Map<String, Object> fetchOneRaw(QueryWrapper qw) {
        qw.limit(1);
        List<Map<String, Object>> data = fetchRaw(qw);
        return data.isEmpty() ? null : data.get(0);
    }

    public static <T extends BeanObject> T fetchOne(QueryWrapper qw, Class<T> beanClass) {
        qw.limit(1);
        List<T> beanObjects = fetch(qw, beanClass);
        return beanObjects.isEmpty() ? null : beanObjects.get(0);
    }

    public static <T extends BeanObject> List<T> fetch(QueryWrapper qw, Class<T> beanClass) {
        List<Map<String, Object>> data = fetchRaw(qw);
        return convertBeanList(data, beanClass);
    }

    public static long fetchCount(QueryWrapper qw) {
        qw.setFetchCount(true);
        List<Map<String, Object>> data = fetchRaw(qw);
        qw.setFetchCount(false);
        if (data.isEmpty()) {
            return 0;
        }
        Map<String, Object> value = data.get(0);
        if (CollectionUtils.isEmpty(value)) {
            return 0;
        }
        return (long) value.values().iterator().next();
    }

    public static <T extends BeanObject> T convertBean(Map<String, Object> data, Class<T> beanClass) {
        EntityTable beanTable = EntityHelper.getEntityTable(beanClass);
        Map<String, String> columnProperties = new HashMap<>();
        for (EntityColumn beanColumn : beanTable.getBeanColumns()) {
            columnProperties.put(beanColumn.getColumn(), beanColumn.getProperty());
        }
        Map<String, Object> beanData = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String property = columnProperties.get(entry.getKey());
            if (StringUtils.isNotEmpty(property)) {
                beanData.put(property, entry.getValue());
            }
        }
        return FactoryManager.fetchFactory(beanClass).convertBean(beanData, beanClass);
    }

    public static <T extends BeanObject> List<T> convertBeanList(List<Map<String, Object>> dataList, Class<T> beanClass) {
        List<T> beanList = new ArrayList<>();
        for (Map<String, Object> data : dataList) {
            beanList.add(convertBean(data, beanClass));
        }
        return beanList;
    }
}
