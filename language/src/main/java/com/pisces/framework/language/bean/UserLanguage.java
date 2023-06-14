package com.pisces.framework.language.bean;

import com.pisces.framework.core.entity.BaseObject;
import com.pisces.framework.language.enums.LAG_LANGUAGE_CODE;
import com.pisces.framework.language.enums.LAG_RESOURCE_TYPE;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "USER_LANGUAGE")
@Entity
public class UserLanguage extends BaseObject {
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
