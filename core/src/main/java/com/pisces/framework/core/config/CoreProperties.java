package com.pisces.framework.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 核心属性
 *
 * @author Jason
 * @date 2022/12/07
 */
@ConfigurationProperties(prefix = "pisces.framework.core")
public class CoreProperties {
    public CoreProperties() {
        int num = 0;
    }

    public Boolean getStrict() {
        return strict;
    }

    public void setStrict(Boolean strict) {
        this.strict = strict;
    }

    private Boolean strict = false;
}
