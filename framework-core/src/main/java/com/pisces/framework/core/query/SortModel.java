package com.pisces.framework.core.query;

import com.pisces.framework.core.enums.VALUE_SORT_TYPE;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jason
 * @date 2023/10/13
 */
@Getter
@Setter
public class SortModel {
    private String propertyCode;
    private VALUE_SORT_TYPE sort;
}
