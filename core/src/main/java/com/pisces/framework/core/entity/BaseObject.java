package com.pisces.framework.core.entity;

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
public class BaseObject implements Comparable<BaseObject> {

    @Id
    private Long id;
    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    @Column(name = "DATA_SET_ID")
    private Long dataSetId;

    public void init() {
        id = IDGenerator.instance.getID();
        createBy = "";
        updateBy = "";
        createDate = new Date();
        updateDate = createDate;
        dataSetId = 0L;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (this.getClass() == obj.getClass()) {
            return this.getId().equals(((BaseObject) obj).getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public int compareTo(BaseObject o) {
        return o.getId().compareTo(this.getId());
    }
}
