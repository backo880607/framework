package com.pisces.framework.web.config;

import com.pisces.framework.core.config.BaseConfiguration;
import com.pisces.framework.web.context.PiscesContext;
import com.pisces.framework.web.servlet.ContextForServlet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 网络自动配置
 *
 * @author jason
 * @date 2022/12/08
 */
@Configuration
@EnableConfigurationProperties(WebProperties.class)
@PropertySource(ignoreResourceNotFound = true, value = "classpath:framework-web.properties")
public class WebAutoConfiguration extends BaseConfiguration {

    /**
     * 获取上下文Bean [ SpringBoot3 Jakarta Servlet 版 ]
     *
     * @return /
     */
    @Bean
    public PiscesContext getContextForServlet() {
        return new ContextForServlet();
    }
}
