package com.pisces.framework.core.dao.impl;

import com.pisces.framework.core.entity.BaseObject;

/**
 * 单修改刀impl
 *
 * @author jason
 * @date 2022/12/07
 */
public class SingletonModifyDaoImpl<T extends BaseObject> extends SingletonDaoImpl<T> {
    private boolean modified;

    public boolean getModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
