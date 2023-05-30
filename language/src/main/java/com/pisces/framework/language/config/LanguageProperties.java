package com.pisces.framework.language.config;

import com.pisces.framework.core.config.BaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 语言属性
 *
 * @author jason
 * @date 2022/12/07
 */
@ConfigurationProperties(prefix = "pisces.framework.language")
public class LanguageProperties extends BaseProperties {
    public LanguageProperties() {
        super(LanguageConstant.IDENTIFY);
    }
}
