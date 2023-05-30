package com.pisces.framework.core.entity;

import com.pisces.framework.core.utils.IDGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseObject implements Comparable<BaseObject> {

    private String createBy;
    private Date createDate;
    private String updateBy;
    private Date updateDate;
    private Long dataSetId;
    private Long id;

    public void init() {
        createBy = "";
        updateBy = "";
        createDate = new Date();
        updateDate = createDate;
        dataSetId = 0L;
        id = IDGenerator.instance.getID();
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
