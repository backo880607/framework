package com.pisces.framework.core.config;

/**
 * 基本属性
 *
 * @author jason
 * @date 2022/12/07
 */
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

    public String getIdentify() {
        return identify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBeanPackage() {
        return beanPackage;
    }

    public String getEnumPackage() {
        return enumPackage;
    }
}
