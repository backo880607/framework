package com.pisces.framework.core.utils.lang;

/**
 * 列跑龙套
 *
 * @author jason
 * @date 2023/06/25
 */
public class ColumnUtils {
    public static void keepColumnSafely(String column) {
        if (StringUtils.isBlank(column)) {
            throw new IllegalArgumentException("Column must not be empty");
        } else {
            column = column.trim();
        }

        int strLen = column.length();
        for (int i = 0; i < strLen; ++i) {
            char ch = column.charAt(i);
            if (Character.isWhitespace(ch)) {
                throw new IllegalArgumentException("Column must not has space char.");
            }
            if (isUnSafeChar(ch)) {
                throw new IllegalArgumentException("Column has unsafe char: [" + ch + "].");
            }
        }
    }

    public static void keepOrderBySqlSafely(String value) {
        // 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
        String SQL_ORDER_BY_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";
        if (!value.matches(SQL_ORDER_BY_PATTERN)) {
            throw new IllegalArgumentException("Order By sql not safe, order by string: " + value);
        }
    }


    private static final char[] UN_SAFE_CHARS = "'`\"<>&*+=#-;".toCharArray();

    private static boolean isUnSafeChar(char ch) {
        for (char c : UN_SAFE_CHARS) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据数据库响应结果判断数据库操作是否成功。
     *
     * @param result 数据库操作返回影响条数
     * @return {@code true} 操作成功，{@code false} 操作失败。
     */
    public static boolean toBool(Number result) {
        return result != null && result.longValue() > 0;
    }
}
