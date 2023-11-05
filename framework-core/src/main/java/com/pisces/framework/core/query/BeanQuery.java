package com.pisces.framework.core.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.core.query.expression.Expression;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jason
 * @date 2023/10/25
 */
@Getter
@Setter
public class BeanQuery {
    @JsonIgnore
    private Expression filter;
    @JsonIgnore
    private List<BeanSortInfo> sortInfo = new ArrayList<>();
    private Integer offset;
    private Integer count;

    private Integer totalSize = 0;
    private List<BeanObject> beans = new ArrayList<>();
}
