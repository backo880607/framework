package com.pisces.framework.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pisces.framework.core.utils.IDGenerator;
import com.pisces.framework.type.annotation.PropertyMeta;
import com.pisces.framework.type.annotation.TableMeta;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.Date;

/**
 * bean对象
 *
 * @author jason
 * @date 2023/06/23
 */
@MappedSuperclass
@TableMeta
public abstract class BeanObject implements Comparable<BeanObject> {
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    private Boolean enabled;
    private Integer tenant;
    @Id
    private Long id;

    @PropertyMeta(property = false)
    @JsonIgnore
    private transient Boolean initialized = false;

    public void init() {
        id = IDGenerator.instance.getID();
        createBy = "";
        updateBy = "";
        createDate = new Date();
        updateDate = createDate;
        tenant = 0;
        enabled = true;
        initialized = true;
    }

    public final Long getId() {
        return id;
    }

    public final void setId(Long value) {
        this.id = value;
    }

    @JsonIgnore
    public final boolean getInitialized() {
        return initialized;
    }

    @JsonIgnore
    public final void setInitialized(boolean value) {
        this.initialized = value;
    }

    public final String getCreateBy() {
        return createBy;
    }

    public final void setCreateBy(String value) {
        this.createBy = value;
    }

    public final String getUpdateBy() {
        return updateBy;
    }

    public final void setUpdateBy(String value) {
        this.updateBy = value;
    }

    public final Date getCreateDate() {
        return createDate;
    }

    public final void setCreateDate(Date value) {
        this.createDate = value;
    }

    public final Date getUpdateDate() {
        return updateDate;
    }

    public final void setUpdateDate(Date value) {
        this.updateDate = value;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getTenant() {
        return tenant;
    }

    public void setTenant(Integer tenant) {
        this.tenant = tenant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (this.getClass() == obj.getClass()) {
            return this.getId().equals(((BeanObject) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public int compareTo(BeanObject o) {
        return o.getId().compareTo(this.getId());
    }
}
