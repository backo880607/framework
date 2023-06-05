package com.pisces.framework.nosql.config;

import com.pisces.framework.core.config.BaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 没有sql特性
 *
 * @author jason
 * @date 2022/12/08
 */
@ConfigurationProperties(prefix = "pisces.framework.nosql")
public class NoSqlProperties extends BaseProperties {
    public NoSqlProperties() {
        super(NoSqlConstant.IDENTIFY);
    }
}
