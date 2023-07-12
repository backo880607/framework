package com.pisces.framework.web.interceptor;

import com.pisces.framework.core.locale.LocaleManager;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.web.config.WebMessage;
import com.pisces.framework.web.controller.ResponseData;
import com.pisces.framework.web.token.JwtTokenHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 令牌拦截器
 *
 * @author jason
 * @date 2023/07/04
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization)) {
            return notAuthorization(response);
        }

        // 是否为HTTP Basic
        if (authorization.startsWith("Basic ")) {
            // 裁剪前缀并解码
            String account = new String(Base64.getDecoder().decode(authorization.substring(6)), StandardCharsets.UTF_8);
            if (StringUtils.hasText(account)) {
                String[] array = account.split(":");
                if (array.length == 2) {
                    String username = array[0];
                    String password = array[1];
                    return true;
                }
            }
        } else {
            final String tokenHead = "Bearer ";
            String authToken = authorization.startsWith(tokenHead) ? authorization.substring(tokenHead.length()) : request.getParameter("token");
            if (StringUtils.hasText(authToken)) {
                try {
                    JwtTokenHelper helper = JwtTokenHelper.parseToken(authToken);
                    if (StringUtils.hasText(helper.getSubject()) && !helper.getExpiration()) {
                        LocaleManager.setLocale(request.getHeader("Accept-Language"));
                        AppUtils.bindAccount(helper.getAccountData());
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return notAuthorization(response);
    }

    private boolean notAuthorization(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json");

        ResponseData data = new ResponseData();
        data.setSuccess(false);
        data.setStatus(WebMessage.UNAUTHORIZED.ordinal());
        data.setName(WebMessage.UNAUTHORIZED.name());
        data.setMessage(LocaleManager.getLanguage(WebMessage.UNAUTHORIZED));
        PrintWriter out = response.getWriter();
        out.write(ObjectUtils.defaultBeanMapper().writeValueAsString(data));
        out.flush();
        out.close();
        return false;
    }
}
