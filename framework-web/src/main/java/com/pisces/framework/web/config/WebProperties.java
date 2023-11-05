package com.pisces.framework.web.config;

import com.pisces.framework.core.config.BaseProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网络属性
 *
 * @author jason
 * @date 2022/12/08
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pisces.framework.web")
public class WebProperties extends BaseProperties {
    private String clientId;
    private String base64Secret;
    private String audience;
    private int expiresSecond;

    public WebProperties() {
        super(WebConstant.IDENTIFY);
    }
}
