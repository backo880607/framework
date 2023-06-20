package com.pisces.framework.web.controller;

import java.io.Serial;

/**
 * 分页返回结果
 *
 * @author Jason
 * @date 2022-02-24
 */
public class ResponsePage extends ResponseData {
    @Serial
    private static final long serialVersionUID = -6944494774943660488L;

    /**
     * 总记录数
     */
    private int total;
    /**
     * 总页数
     */
    private int totalPage;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
