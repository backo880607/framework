package com.pisces.framework.nosql.model;

import java.util.List;

/**
 * 复述,同步模式
 *
 * @author jason
 * @date 2022/12/08
 */
public class RedisSyncModel {
    private String bean;
    private Long id;
    private List<Long> idList;

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
