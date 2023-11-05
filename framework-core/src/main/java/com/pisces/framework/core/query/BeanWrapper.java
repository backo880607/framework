package com.pisces.framework.core.query;

import com.pisces.framework.core.query.column.QueryColumn;
import com.pisces.framework.core.query.condition.QueryCondition;
import com.pisces.framework.core.query.condition.QueryOrderBy;

import java.io.Serializable;
import java.util.List;

public class BeanWrapper implements Serializable {
    private QueryCondition whereCondition;
    private List<QueryColumn> groupByColumns;
    private QueryCondition havingCondition;
    private List<QueryOrderBy> orderBys;

    private Integer limitOffset;
    private Integer limitRows;
}
