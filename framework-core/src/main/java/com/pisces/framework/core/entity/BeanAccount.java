package com.pisces.framework.core.entity;

import com.pisces.framework.core.validator.constraints.PrimaryKey;
import com.pisces.framework.core.validator.group.InsertGroup;
import com.pisces.framework.core.validator.group.UpdateGroup;
import com.pisces.framework.type.annotation.TableMeta;
import lombok.Getter;
import lombok.Setter;

/**
 * 实体账户
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
@Setter
@TableMeta
@PrimaryKey(fields = {"account"}, groups = {InsertGroup.class, UpdateGroup.class})
public class BeanAccount extends BeanObject {
    private String account;

    @Override
    public void init() {
        super.init();
        account = "";
    }
}
