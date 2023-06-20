package com.pisces.framework.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pisces.framework.core.annotation.PropertyMeta;
import com.pisces.framework.core.utils.IDGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class BeanObject implements Comparable<BeanObject> {

    @Id
    private Long id;
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    @Column(name = "DATA_SET_ID")
    private Long dataSetId;

    @JsonIgnore
    @PropertyMeta(property = false)
    private transient boolean initialized = false;

    public void init() {
        id = IDGenerator.instance.getID();
        createBy = "";
        updateBy = "";
        createDate = new Date();
        updateDate = createDate;
        dataSetId = 0L;
        initialized = true;
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
