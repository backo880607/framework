package com.pisces.framework.core.query.expression;

/**
 * ③类型
 *
 * @author jason
 * @date 2022/12/07
 */
public enum OperationType {
    /**
     * 数据
     */
    DATA(0),
    /**
     * 左圆括号
     */
    LPAREN(1),
    /**
     * 右圆括号
     */
    RPAREN(1),
    /**
     * 左方括号
     */
    LSQUARE(1),
    /**
     * 右方括号
     */
    RSQUARE(1),
    /**
     * 点，成员选择（对象）
     */
    POINT(1),
    /**
     * 负号
     */
    NEGATIVE(2),
    /**
     * 自增运算符
     */
    SELFINCRE(2),
    /**
     * 非
     */
    NOT(2),
    /**
     * 乘
     */
    MULTIPLIED(3),
    /**
     * 除
     */
    DIVIDED(3),
    /**
     * 取模
     */
    MOD(3),
    /**
     * 加
     */
    PLUS(4),
    /**
     * 减
     */
    MINUS(4),
    /**
     * 大于
     */
    GREATER(6),
    /**
     * 大于等于
     */
    GREATEREQUAL(6),
    /**
     * 小于
     */
    LESS(6),
    /**
     * 小于等于
     */
    LESSEQUAL(6),
    /**
     * 等于
     */
    EQUAL(7),
    /**
     * 不等于
     */
    NOTEQUAL(7),
    /**
     * 且
     */
    AND(11),
    /**
     * 或
     */
    OR(12),
    /**
     * 函数
     */
    FUN(16),
    /**
     * 非法
     */
    EOL(17);

    private final int value;

    OperationType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
