package com.pisces.framework.core.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 页面参数
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
@Setter
public class PageParam {
    private int pageNum;
    private int pageSize;
    private String orderBy;
    private String filter;
}
