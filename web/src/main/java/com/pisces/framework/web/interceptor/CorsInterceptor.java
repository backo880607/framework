package com.pisces.framework.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 歌珥拦截器
 *
 * @author jason
 * @date 2022/12/08
 */
public class CorsInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getHeader(HttpHeaders.ORIGIN) != null) {
            String origin = request.getHeader("Origin");
            response.addHeader("Access-Control-Allow-Origin", origin);
            response.addHeader("Access-Control-Allow-Credentials", "true");
            response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT,PATCH, HEAD");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.addHeader("Access-Control-Max-Age", "3600");
        }
        return true;
    }
}
