package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.utils.lang.ObjectUtils;

/**
 * 实体计算
 *
 * @author jason
 * @date 2022/12/07
 */
public class BeanCalculate implements Calculate {
    private Class<?> clazz;

    @Override
    public int parse(String str, int index) {
        int temp = index;
        while (index < str.length()) {
            char curChar = str.charAt(index);
            if (!Character.isAlphabetic(curChar) && !Character.isDigit(curChar)) {
                String name = str.substring(temp, index);
                clazz = ObjectUtils.fetchBeanClass(name);
                if (clazz == null) {
                    return -1;
                }
                return index;
            }
            ++index;
        }

        return -1;
    }

    @Override
    public Class<?> getReturnClass() {
        return clazz;
    }
}
