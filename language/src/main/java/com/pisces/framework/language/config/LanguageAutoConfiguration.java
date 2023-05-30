package com.pisces.framework.language.config;

import com.pisces.framework.core.config.BaseConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 语言自动配置
 *
 * @author jason
 * @date 2022/12/07
 */
@Configuration
@EnableConfigurationProperties({LanguageProperties.class})
@PropertySource(ignoreResourceNotFound = true, value = "classpath:language.properties")
public class LanguageAutoConfiguration extends BaseConfiguration {
}
