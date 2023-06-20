package com.pisces.framework.nosql.bean;

import com.pisces.framework.core.entity.BeanObject;
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
@Table(name = "NOSQL_TEST")
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
