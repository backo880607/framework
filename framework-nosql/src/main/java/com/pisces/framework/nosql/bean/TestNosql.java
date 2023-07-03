package com.pisces.framework.nosql.bean;

import com.pisces.framework.core.entity.BeanObject;
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
@TableMeta(name = "NOSQL_TEST")
public class TestNosql extends BeanObject {
    private String module;
    private String resKey;
    private String value;

    @Override
    public void init() {
        super.init();
        this.module = "";
        this.resKey = "";
        this.value = "";
    }
}
