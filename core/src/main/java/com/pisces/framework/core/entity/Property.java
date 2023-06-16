package com.pisces.framework.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pisces.framework.core.annotation.PropertyMeta;
import com.pisces.framework.core.enums.PROPERTY_TYPE;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 财产
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
@Setter
@Table(name = "PROPERTY")
public class Property extends BaseObject {
    private String propertyCode;
    private String propertyName;

    /**
     * 属性类型
     */
    private PROPERTY_TYPE type;
    /**
     * 属性类型简称
     */
    private String typeName;
    /**
     * 是否能编辑
     */
    private Boolean modify;
    /**
     * 是否不为空
     */
    private Boolean notEmpty;
    /**
     * 是否隐藏
     */
    private Boolean hide;
    /**
     * 浮点数的精度控制
     */
    private Integer precis;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否弹框查找显示
     */
    private Boolean popDisplay;
    /**
     * 编辑类型
     */
    private String editType;
    /**
     * 提示
     */
    private String tooltip;
    /**
     * 属性所属类
     */
    @PropertyMeta(system = true)
    private String belongName;
    /**
     * 属性类型全名，包含包路径
     */
    @PropertyMeta(system = true)
    private String typeFullName;
    /**
     * 是否为长度超大的数据类型
     */
    @PropertyMeta(property = false)
    private Boolean large;
    /**
     * 对于系统属性，不应显示在界面中
     */
    @PropertyMeta(system = true)
    private Boolean system;
    /**
     * 是否为主键字段，自定义字段不能作为主键
     */
    @PropertyMeta(system = true)
    private Boolean primaryKey;
    /**
     * 枚举类
     */
    @PropertyMeta(property = false)
    private transient Class<Enum<?>> enumClass;
    /**
     * 枚举项
     */
    @PropertyMeta(property = false)
    private transient List<EnumDto> enumItems;
    /**
     * 排序号
     */
    @PropertyMeta(modify = false)
    private Integer orderNumber;

    @JsonIgnore
    @PropertyMeta(property = false)
    private transient Class<? extends BaseObject> belongClass;
    @JsonIgnore
    @PropertyMeta(property = false)
    private transient Class<?> typeClass;
    @JsonIgnore
    @PropertyMeta(property = false)
    private transient Method getMethod;
    @JsonIgnore
    @PropertyMeta(property = false)
    private transient Method setMethod;
    @JsonIgnore
    @PropertyMeta(property = false)
    private transient Field field;

    @Override
    public void init() {
        super.init();
        this.propertyCode = "";
        this.propertyName = "";
        this.belongName = "";
        this.type = PROPERTY_TYPE.STRING;
        this.typeName = "";
        this.typeFullName = "";
        this.large = false;
        this.system = false;
        this.primaryKey = false;
        this.hide = false;
        this.precis = 2;
        this.modify = true;
        this.notEmpty = false;
        this.popDisplay = false;
        this.defaultValue = "";
        this.orderNumber = 0;
    }
}
