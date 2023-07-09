package com.pisces.framework.web.interceptor;

import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.web.token.JwtTokenHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;

/**
 * 令牌拦截器
 *
 * @author jason
 * @date 2023/07/04
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        //先从url中取token
        final String tokenHeader = "Authorization";
        final String tokenHead = "Bearer ";
        String authToken = request.getParameter("token");
        String authHeader = request.getHeader(tokenHeader);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(tokenHead)) {
            //如果header中存在token，则覆盖掉url中的token
            authToken = authHeader.substring(tokenHead.length());
        }
        if (!StringUtils.hasText(authToken)) {
            return false;
        }
        JwtTokenHelper helper = JwtTokenHelper.parseToken(authToken);
        if (!StringUtils.hasText(helper.getSubject()) || helper.getExpiration()) {
            return false;
        }

        LocaleManager.setLocale(request.getHeader("Accept-Language"));
        AppUtils.bindAccount(helper.getAccountData());
        return true;
    }
}
