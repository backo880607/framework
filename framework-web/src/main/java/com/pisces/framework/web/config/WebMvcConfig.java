package com.pisces.framework.web.config;

import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.web.converter.StringToLong;
import com.pisces.framework.web.interceptor.LogInterceptor;
import com.pisces.framework.web.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * web mvc配置
 *
 * @author jason
 * @date 2022/12/08
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new TokenInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/Account/login");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLong());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(ObjectUtils.createBeanMapper());
        converter.setPrettyPrint(false);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypes);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        // 放到第一个
        converters.add(0, converter);
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver("messages");
        cookieLocaleResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return cookieLocaleResolver;
    }

    private CorsConfiguration buildConfig() {
        // 配置跨域
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许哪个请求来源进行跨域
        corsConfiguration.addAllowedOriginPattern("*");
        // 是否允许携带cookie进行跨域
        corsConfiguration.setAllowCredentials(true);
        // 允许哪个请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许哪个方法进行跨域
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("Authorization");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
