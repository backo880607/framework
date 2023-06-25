package com.pisces.framework.core.enums;

/**
 * 条件类型
 *
 * @author jason
 * @date 2023/06/25
 */
public enum CONDITION_TYPE {
    /**
     * 包含
     */
    CONTAINS,
    /**
     * 不包含
     */
    NOT_CONTAINS,
    /**
     * 相等
     */
    EQUAL,
    /**
     * 不相等
     */
    NOT_EQUAL,
    /**
     * 小于
     */
    LESS,
    /**
     * 小于等于
     */
    LESS_EQUAL,
    /**
     * 大于
     */
    GREATER,
    /**
     * 大于等于
     */
    GREATER_EQUAL,
    /**
     * 之间，左闭右闭
     */
    BETWEEN,
    /**
     * 非区间，左开右开
     */
    NOT_BETWEEN,
    /**
     * 开始于
     */
    START_WITH,
    /**
     * 非开始于
     */
    NOT_START_WITH,
    /**
     * 结束于
     */
    END_WITH,
    /**
     * 非结束于
     */
    NOT_END_WITH,
    /**
     * 空
     */
    EMPTY,
    /**
     * 非空
     */
    NOT_EMPTY,
    /**
     * 在范围内
     */
    IN_RANGE,
    /**
     * 在范围之外
     */
    NOT_IN_RANGE,
}
