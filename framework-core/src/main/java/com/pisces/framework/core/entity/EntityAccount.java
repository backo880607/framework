package com.pisces.framework.core.entity;

import com.pisces.framework.core.validator.constraints.PrimaryKey;
import com.pisces.framework.core.validator.group.InsertGroup;
import com.pisces.framework.core.validator.group.UpdateGroup;
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
@PrimaryKey(fields = {"account"}, groups = {InsertGroup.class, UpdateGroup.class})
public class EntityAccount extends BeanObject {
    private String account;

    @Override
    public void init() {
        super.init();
        account = "";
    }
}
