package com.pisces.framework.core.config;

import lombok.Getter;

/**
 * 基本属性
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public abstract class BaseProperties {
    private final String identify;
    private String name;
    private String author;
    private String description;
    private final String beanPackage;
    private final String enumPackage;

    protected BaseProperties(String identify) {
        this.identify = identify;
        String classPath = this.getClass().getName();
        String modelPath = classPath.substring(0, classPath.indexOf(identify) + identify.length() + 1);
        this.beanPackage = modelPath + "bean";
        this.enumPackage = modelPath + "enums";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
