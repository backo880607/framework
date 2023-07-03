package com.pisces.framework.language.bean;

import com.pisces.framework.core.entity.BeanObject;
import com.pisces.framework.language.enums.LAG_LANGUAGE_CODE;
import com.pisces.framework.language.enums.LAG_RESOURCE_TYPE;
import com.pisces.framework.type.annotation.TableMeta;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户语言
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
@Setter
@TableMeta(name = "USER_LANGUAGE")
public class UserLanguage extends BeanObject {
    private LAG_LANGUAGE_CODE language;
    private LAG_RESOURCE_TYPE type;
    private String module;
    private String resKey;
    private String value;

    @Override
    public void init() {
        super.init();
        this.language = LAG_LANGUAGE_CODE.ZH_CN;
        this.type = LAG_RESOURCE_TYPE.OTHER;
        this.module = "";
        this.resKey = "";
        this.value = "";
    }
}
