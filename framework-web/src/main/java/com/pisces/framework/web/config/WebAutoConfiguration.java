package com.pisces.framework.web.config;

import com.pisces.framework.core.config.BaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@PropertySource(ignoreResourceNotFound = true, value = "classpath:web.properties")
public class WebAutoConfiguration extends BaseConfiguration {
}
