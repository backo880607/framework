package com.pisces.framework.web.context;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.core.utils.lang.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 双鱼座饼干
 *
 * @author jason
 * @date 2023/07/09
 */
@Getter
@Setter
public class PiscesCookie {
    /**
     * 写入响应头时使用的key
     */
    public static final String HEADER_NAME = "Set-Cookie";

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;

    /**
     * 有效时长 （单位：秒），-1代表为临时Cookie 浏览器关闭后自动删除
     */
    private int maxAge = -1;

    /**
     * 域
     */
    private String domain;

    /**
     * 路径
     */
    private String path;

    /**
     * 是否只在 https 协议下有效
     */
    private Boolean secure = false;

    /**
     * 是否禁止 js 操作 Cookie
     */
    private Boolean httpOnly = false;

    /**
     * 第三方限制级别（Strict=完全禁止，Lax=部分允许，None=不限制）
     */
    private String sameSite;

    /**
     * 构造一个
     */
    public PiscesCookie() {
    }

    /**
     * 构造一个
     *
     * @param name  名字
     * @param value 值
     */
    public PiscesCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 构建一下
     */
    public void builde() {
        if (path == null) {
            path = "/";
        }
    }

    /**
     * 转换为响应头 Set-Cookie 参数需要的值
     *
     * @return /
     */
    public String toHeaderValue() {
        this.builde();

        if (StringUtils.isEmpty(name)) {
            throw new SystemException("name不能为空");
        }
        if (value != null && value.contains(";")) {
            throw new SystemException("无效Value：" + value);
        }

        // Set-Cookie: name=value; Max-Age=100000; Expires=Tue, 05-Oct-2021 20:28:17 GMT; Domain=localhost; Path=/; Secure; HttpOnly; SameSite=Lax

        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);

        if (maxAge >= 0) {
            sb.append("; Max-Age=").append(maxAge);
            String expires;
            if (maxAge == 0) {
                expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            } else {
                expires = OffsetDateTime.now().plusSeconds(maxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            }
            sb.append("; Expires=").append(expires);
        }
        if (!StringUtils.isEmpty(domain)) {
            sb.append("; Domain=").append(domain);
        }
        if (!StringUtils.isEmpty(path)) {
            sb.append("; Path=").append(path);
        }
        if (secure) {
            sb.append("; Secure");
        }
        if (httpOnly) {
            sb.append("; HttpOnly");
        }
        if (!StringUtils.isEmpty(sameSite)) {
            sb.append("; SameSite=").append(sameSite);
        }

        return sb.toString();
    }
}
