package com.pisces.framework.language.enums;

/**
 * 滞后语言代码
 *
 * @author jason
 * @date 2022/12/07
 */
public enum LAG_LANGUAGE_CODE {
    /**
     * 中文(简体)
     */
    ZH_CN("zh-CN", "中文(简体)"),
    ZH_TW("zh-TW", "中文(繁体)"),
    JA_JP("ja-JP", "日语"),
    EN_US("en-US", "英语(美国)");

    private final String code;
    private final String name;

    LAG_LANGUAGE_CODE(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
