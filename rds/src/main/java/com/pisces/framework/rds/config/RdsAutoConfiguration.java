package com.pisces.framework.rds.config;

import com.pisces.framework.core.config.BaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * rds汽车配置
 *
 * @author jason
 * @date 2022/12/07
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RdsProperties.class)
@PropertySource(ignoreResourceNotFound = true, value = "classpath:rds.properties")
public class RdsAutoConfiguration extends BaseConfiguration {
}
