package com.pisces.framework.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 核心汽车配置
 *
 * @author Jason
 * @date 2022/12/07
 */
@Configuration
@EnableConfigurationProperties(CoreProperties.class)
@PropertySource(ignoreResourceNotFound = true, value = "classpath:core.properties")
public class CoreAutoConfiguration {
}
