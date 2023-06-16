package com.pisces.framework.core.entity.factory;

import com.pisces.framework.core.entity.BaseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体工厂
 *
 * @author jason
 * @date 2022/12/07
 */
public final class EntityFactory {
    private final Class<? extends BaseObject> objectClass;
    String identify = "";
    EntityFactory superFactory = null;
    List<EntityFactory> childFactories = new ArrayList<>();
    int maxSign = 0;

    public EntityFactory(Class<? extends BaseObject> value) {
        this.objectClass = value;
    }

    public <T extends BaseObject> Class<T> getEntityClass() {
        return (Class<T>) this.objectClass;
    }

    public EntityFactory getSuperFactory() {
        return this.superFactory;
    }

    public List<EntityFactory> getChildFactories() {
        return this.childFactories;
    }

    public int getMaxSign() {
        return maxSign;
    }
}
