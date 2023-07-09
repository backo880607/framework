package com.pisces.framework.web.servlet;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.web.context.PiscesContext;
import com.pisces.framework.web.context.PiscesRequest;
import com.pisces.framework.web.context.PiscesResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * servlet请求
 *
 * @author jason
 * @date 2023/07/09
 */
public class RequestForServlet implements PiscesRequest {
    /**
     * 底层Request对象
     */
    protected HttpServletRequest request;

    /**
     * 实例化
     *
     * @param request request对象
     */
    public RequestForServlet(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return request;
    }

    /**
     * 在 [请求体] 里获取一个值
     */
    @Override
    public String getParam(String name) {
        return request.getParameter(name);
    }

    /**
     * 在 [请求头] 里获取一个值
     */
    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    /**
     * 在 [Cookie作用域] 里获取一个值
     */
    @Override
    public String getCookieValue(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 返回当前请求path (不包括上下文名称)
     */
    @Override
    public String getRequestPath() {
        return request.getServletPath();
    }

    /**
     * 返回当前请求的url，例：http://xxx.com/test
     *
     * @return see note
     */
    @Override
    public String getUrl() {
        return request.getRequestURL().toString();
    }

    /**
     * 返回当前请求的类型
     */
    @Override
    public String getMethod() {
        return request.getMethod();
    }

    /**
     * 转发请求
     */
    @Override
    public Object forward(String path) {
        try {
            PiscesResponse response = AppUtils.getBean(PiscesContext.class).getResponse();
            request.getRequestDispatcher(path).forward(request, (ServletResponse) response.getSource());
            return null;
        } catch (ServletException | IOException e) {
            throw new SystemException(e);
        }
    }
}
