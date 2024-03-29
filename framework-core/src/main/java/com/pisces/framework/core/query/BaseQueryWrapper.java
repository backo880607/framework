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
package com.pisces.framework.core.query;

import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.query.condition.QueryConnector;
import com.pisces.framework.core.query.condition.QueryOrderBy;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * 基础查询包装
 *
 * @author jason
 * @date 2023/06/25
 */
@Getter
@Setter
public class BaseQueryWrapper implements Serializable {
    protected List<QueryTable> queryTables;
    private String hint;

    protected List<QueryColumn> selectColumns;
    private List<QueryTable> joinTables;
    private QueryCondition whereCondition;
    private List<QueryColumn> groupByColumns;
    private QueryCondition havingCondition;
    private List<QueryOrderBy> orderBys;

    private Integer limitOffset;
    private Integer limitRows;
    private Boolean fetchCount;

    protected Map<String, Object> context;

    public List<QueryTable> getAllTables() {
        return CollectionUtils.merge(queryTables, joinTables);
    }

    protected void addSelectColumn(QueryColumn queryColumn) {
        if (selectColumns == null) {
            selectColumns = new LinkedList<>();
        }

        selectColumns.add(queryColumn);
    }

    protected void setWhereCondition(QueryCondition queryCondition) {
        if (whereCondition != null) {
            queryCondition.connect(whereCondition, QueryConnector.AND);
        }

        whereCondition = queryCondition;
    }

    public void addWhereQueryCondition(QueryCondition queryCondition, QueryConnector connector) {
        if (queryCondition == null) {
            return;
        }
        if (whereCondition == null) {
            whereCondition = queryCondition;
        } else {
            whereCondition.connect(queryCondition, connector);
        }
    }

    protected void addGroupByColumns(QueryColumn queryColumn) {
        if (groupByColumns == null) {
            groupByColumns = new LinkedList<>();
        }

        groupByColumns.add(queryColumn);
    }

    protected void addHavingQueryCondition(QueryCondition queryCondition, QueryConnector connector) {
        if (havingCondition == null) {
            havingCondition = queryCondition;
        } else {
            havingCondition.connect(queryCondition, connector);
        }
    }

    protected void addOrderBy(QueryOrderBy queryOrderBy) {
        if (orderBys == null) {
            orderBys = new LinkedList<>();
        }
        orderBys.add(queryOrderBy);
    }

    protected void addJoinTable(QueryTable queryTable) {
        if (joinTables == null) {
            joinTables = new ArrayList<>();
        }
        joinTables.add(queryTable);
    }

    protected void putContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    protected <R> R getContext(String key) {
        return context == null ? null : (R) context.get(key);
    }
}