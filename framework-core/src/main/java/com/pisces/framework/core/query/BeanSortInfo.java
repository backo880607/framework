package com.pisces.framework.core.query;

import com.pisces.framework.core.enums.VALUE_SORT_TYPE;
import com.pisces.framework.core.query.expression.Expression;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jason
 * @date 2023/11/03
 */
@Getter
@Setter
public class BeanSortInfo {
    private Expression expression;
    private VALUE_SORT_TYPE sort;
}

