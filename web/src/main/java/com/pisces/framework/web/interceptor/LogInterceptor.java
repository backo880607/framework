package com.pisces.framework.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 记录拦截器
 *
 * @author jason
 * @date 2022/12/08
 */
public class LogInterceptor implements HandlerInterceptor {
    static Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("请求路径：{}", request.getRequestURI());
        return true;
    }
}
