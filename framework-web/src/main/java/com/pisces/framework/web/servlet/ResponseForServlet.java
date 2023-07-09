package com.pisces.framework.web.servlet;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.web.context.PiscesResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 对servlet响应
 *
 * @author jason
 * @date 2023/07/09
 */
public class ResponseForServlet implements PiscesResponse {
    /**
     * 底层Request对象
     */
    protected HttpServletResponse response;

    /**
     * 实例化
     *
     * @param response response对象
     */
    public ResponseForServlet(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return response;
    }

    /**
     * 在响应头里写入一个值
     */
    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        response.addHeader(name, value);
    }

    @Override
    public void setStatus(int sc) {
        response.setStatus(sc);
    }

    @Override
    public Object redirect(String url) {
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            throw new SystemException(e);
        }
        return null;
    }
}
