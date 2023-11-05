package com.pisces.framework.core.query.condition;

import java.util.ArrayList;
import java.util.List;

public class QueryGroup {
    private List<QueryCondition> querys = new ArrayList<>();
    //两个条件直接的连接符
    private QueryConnector connector;
}
