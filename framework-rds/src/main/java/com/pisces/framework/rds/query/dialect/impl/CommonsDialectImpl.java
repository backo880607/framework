/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.rds.query.dialect.impl;

import com.pisces.framework.core.query.QueryMethods;
import com.pisces.framework.core.query.QueryOrderBy;
import com.pisces.framework.core.query.QueryTable;
import com.pisces.framework.core.query.QueryWrapper;
import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.Guard;
import com.pisces.framework.core.utils.lang.StringUtils;
import com.pisces.framework.rds.config.RdsConstant;
import com.pisces.framework.rds.query.SqlTools;
import com.pisces.framework.rds.query.dialect.IDialect;
import com.pisces.framework.rds.query.dialect.KeywordWrap;
import com.pisces.framework.rds.query.dialect.LimitOffsetProcessor;

import java.util.List;

/**
 * 通用的方言设计，其他方言可以继承于当前 CommonsDialectImpl
 *
 * @author jason
 * @date 2023/06/27
 */
public class CommonsDialectImpl implements IDialect {

    protected KeywordWrap keywordWrap = KeywordWrap.BACKQUOTE;
    private LimitOffsetProcessor limitOffsetProcessor = LimitOffsetProcessor.MYSQL;

    public CommonsDialectImpl() {
    }

    public CommonsDialectImpl(LimitOffsetProcessor limitOffsetProcessor) {
        this.limitOffsetProcessor = limitOffsetProcessor;
    }

    public CommonsDialectImpl(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        this.keywordWrap = keywordWrap;
        this.limitOffsetProcessor = limitOffsetProcessor;
    }

    @Override
    public String wrap(String keyword) {
        return keywordWrap.wrap(keyword);
    }

    @Override
    public String forHint(String hintString) {
        return StringUtils.isNotBlank(hintString) ? "/*+ " + hintString + " */ " : "";
    }

    @Override
    public String forDeleteById(String tableName, String[] primaryKeys) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(wrap(tableName));
        sql.append(" WHERE ");
        for (int i = 0; i < primaryKeys.length; i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append(wrap(primaryKeys[i])).append(" = ?");
        }
        return sql.toString();
    }

    @Override
    public String forDeleteBatchByIds(String tableName, String[] primaryKeys, Object[] ids) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(wrap(tableName));
        sql.append(" WHERE ");

        //多主键的场景
        if (primaryKeys.length > 1) {
            for (int i = 0; i < ids.length / primaryKeys.length; i++) {
                if (i > 0) {
                    sql.append(" OR ");
                }
                sql.append("(");
                for (int j = 0; j < primaryKeys.length; j++) {
                    if (j > 0) {
                        sql.append(" AND ");
                    }
                    sql.append(wrap(primaryKeys[j])).append(" = ?");
                }
                sql.append(")");
            }
        }
        // 单主键
        else {
            for (int i = 0; i < ids.length; i++) {
                if (i > 0) {
                    sql.append(" OR ");
                }
                sql.append(wrap(primaryKeys[0])).append(" = ?");
            }
        }
        return sql.toString();
    }

    protected String buildQuestion(int count, boolean withBrackets) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("?");
            if (i != count - 1) {
                sb.append(", ");
            }
        }
        return withBrackets ? "(" + sb + ")" : sb.toString();
    }

    ////////////build query sql///////
    @Override
    public String buildSelectSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = queryWrapper.getQueryTables();
        List<QueryTable> joinTables = queryWrapper.getJoinTables();
        List<QueryTable> allTables = CollectionUtils.merge(queryTables, joinTables);

        List<QueryColumn> selectColumns = queryWrapper.getSelectColumns();

        StringBuilder sqlBuilder = buildSelectColumnSql(allTables, selectColumns, queryWrapper.getHint(), queryWrapper.getFetchCount());
        sqlBuilder.append(" FROM ").append(StringUtils.join(queryTables, ", ", queryTable -> SqlTools.toSql(queryTable, this)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, true);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);
        buildOrderBySql(sqlBuilder, queryWrapper, allTables);

        Integer limitRows = queryWrapper.getLimitRows();
        Integer limitOffset = queryWrapper.getLimitOffset();
        if (limitRows != null || limitOffset != null) {
            sqlBuilder = buildLimitOffsetSql(sqlBuilder, queryWrapper, limitRows, limitOffset);
        }

        return sqlBuilder.toString();
    }

    private StringBuilder buildSelectColumnSql(List<QueryTable> queryTables, List<QueryColumn> selectColumns, String hint, Boolean fetchCount) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append(forHint(hint));
        if (Guard.value(fetchCount)) {
            sqlBuilder.append(SqlTools.toSelectSql(queryTables, QueryMethods.count(), this));
        } else if (CollectionUtils.isEmpty(selectColumns)) {
            sqlBuilder.append("*");
        } else {
            int index = 0;
            for (QueryColumn selectColumn : selectColumns) {
                if (index > 0) {
                    sqlBuilder.append(RdsConstant.DELIMITER);
                }
                String selectColumnSql = SqlTools.toSelectSql(queryTables, selectColumn, this);
                sqlBuilder.append(selectColumnSql);
                index++;
            }
        }
        return sqlBuilder;
    }

    @Override
    public String buildDeleteSql(QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = queryWrapper.getQueryTables();
        List<QueryTable> joinTables = queryWrapper.getJoinTables();
        List<QueryTable> allTables = CollectionUtils.merge(queryTables, joinTables);

        //ignore selectColumns
        StringBuilder sqlBuilder = new StringBuilder("DELETE " + forHint(queryWrapper.getHint()) + "FROM ");
//        sqlBuilder.append(StringUtils.join(queryTables, ", ", queryTable -> queryTable.toSql(this)));

        buildJoinSql(sqlBuilder, queryWrapper, allTables);
        buildWhereSql(sqlBuilder, queryWrapper, allTables, false);
        buildGroupBySql(sqlBuilder, queryWrapper, allTables);
        buildHavingSql(sqlBuilder, queryWrapper, allTables);

        return sqlBuilder.toString();
    }

    @Override
    public String getAsKeyWord() {
        return RdsConstant.AS;
    }

    protected void buildJoinSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
//        List<Join> joins = CPI.getJoins(queryWrapper);
//        if (joins != null && !joins.isEmpty()) {
//            for (Join join : joins) {
//                if (!join.checkEffective()) {
//                    continue;
//                }
//                sqlBuilder.append(join.toSql(queryTables, this));
//            }
//        }
    }

    protected void buildWhereSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables, boolean allowNoCondition) {
        QueryCondition whereQueryCondition = queryWrapper.getWhereQueryCondition();
        if (whereQueryCondition != null) {
            String whereSql = SqlTools.toSql(queryTables, whereQueryCondition, this);
            if (StringUtils.isNotBlank(whereSql)) {
                sqlBuilder.append(" WHERE ").append(whereSql);
            } else if (!allowNoCondition) {
                throw new IllegalArgumentException("Not allowed DELETE a table without where condition.");
            }
        }
    }

    protected void buildGroupBySql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<QueryColumn> groupByColumns = queryWrapper.getGroupByColumns();
        if (groupByColumns != null && !groupByColumns.isEmpty()) {
            sqlBuilder.append(" GROUP BY ");
            int index = 0;
            for (QueryColumn groupByColumn : groupByColumns) {
                String groupBy = SqlTools.toConditionSql(queryTables, groupByColumn, this);
                sqlBuilder.append(groupBy);
                if (index != groupByColumns.size() - 1) {
                    sqlBuilder.append(", ");
                }
                index++;
            }
        }
    }

    protected void buildHavingSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        QueryCondition havingQueryCondition = queryWrapper.getHavingQueryCondition();
        if (havingQueryCondition != null) {
            String havingSql = SqlTools.toSql(queryTables, havingQueryCondition, this);
            if (StringUtils.isNotBlank(havingSql)) {
                sqlBuilder.append(" HAVING ").append(havingSql);
            }
        }
    }

    protected void buildOrderBySql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, List<QueryTable> queryTables) {
        List<QueryOrderBy> orderBys = queryWrapper.getOrderBys();
        if (orderBys != null && !orderBys.isEmpty()) {
            sqlBuilder.append(" ORDER BY ");
            int index = 0;
            for (QueryOrderBy orderBy : orderBys) {
                sqlBuilder.append(SqlTools.toSql(queryTables, orderBy, this));
                if (index != orderBys.size() - 1) {
                    sqlBuilder.append(", ");
                }
                index++;
            }
        }
    }

    /**
     * 构建 limit 和 offset 的参数
     */
    protected StringBuilder buildLimitOffsetSql(StringBuilder sqlBuilder, QueryWrapper queryWrapper, Integer limitRows, Integer limitOffset) {
        return limitOffsetProcessor.process(sqlBuilder, queryWrapper, limitRows, limitOffset);
    }
}
