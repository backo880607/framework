package com.pisces.framework.web.controller.request;

import com.pisces.framework.core.config.CoreMessage;
import com.pisces.framework.core.enums.VALUE_SORT_TYPE;
import com.pisces.framework.core.query.BeanQuery;
import com.pisces.framework.core.query.BeanSortInfo;
import com.pisces.framework.core.query.expression.Expression;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.Guard;
import com.pisces.framework.core.utils.lang.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jason
 * @date 2023/11/03
 */
@Getter
@Setter
public class PagerRequest {
    @Getter
    @Setter
    public static class SortInfo {
        private String field;
        private VALUE_SORT_TYPE sort;
    }

    private String filter;
    private List<SortInfo> sortInfo = new ArrayList<>();
    private Integer startRow;
    private Integer pageNum;
    private Integer pageSize;

    public BeanQuery convert() {
        BeanQuery query = new BeanQuery();
        if (startRow != null && startRow >= 0) {
            query.setOffset(startRow);
        } else if (pageNum != null && pageNum > 0 && pageSize != null && pageSize > 0) {
            query.setOffset((pageNum - 1) * pageSize);
        }
        query.setCount(pageSize);
        if (StringUtils.isNotBlank(filter)) {
            Expression filterExp = new Expression();
            if (filterExp.parse(filter)) {
                query.setFilter(filterExp);
            }
        }
        if (CollectionUtils.isNotEmpty(sortInfo)) {
            for (SortInfo info : sortInfo) {
                Expression sortExp = new Expression();
                Guard.assertTrue(sortExp.parse(info.getField()), CoreMessage.ExpressionError, info.getField());
                BeanSortInfo beanSort = new BeanSortInfo();
                beanSort.setExpression(sortExp);
                beanSort.setSort(info.getSort());
                query.getSortInfo().add(beanSort);
            }
        }
        return query;
    }
}
