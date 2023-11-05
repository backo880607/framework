package com.pisces.framework.core.query.expression;

import com.pisces.framework.core.enums.VALUE_SORT_TYPE;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpressionOrderBy {
    private Expression expression;
    private VALUE_SORT_TYPE type;
}
