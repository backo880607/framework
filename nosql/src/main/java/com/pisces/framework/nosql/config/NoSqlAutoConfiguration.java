package com.pisces.framework.nosql.config;

import com.pisces.framework.core.config.BaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 任何sql自动配置
 *
 * @author jason
 * @date 2022/12/08
 */
@Configuration
@EnableConfigurationProperties({NoSqlProperties.class})
@PropertySource(ignoreResourceNotFound = true, value = "classpath:nosql.properties")
public class NoSqlAutoConfiguration extends BaseConfiguration {
}
