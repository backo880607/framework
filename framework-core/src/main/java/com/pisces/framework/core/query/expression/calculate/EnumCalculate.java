package com.pisces.framework.core.query.expression.calculate;

import com.pisces.framework.core.entity.EnumHelper;
import com.pisces.framework.core.exception.ExpressionException;
import lombok.Getter;

/**
 * enum calculate
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public class EnumCalculate implements Calculate {
    private Enum<?> value;

    @Override
    public int parse(String str, int index) throws ExpressionException {
        int temp = index;
        Class<?> enumClass = null;
        while (index < str.length()) {
            char curChar = str.charAt(index);
            if (curChar == '.') {
                String name = str.substring(temp, index);
                enumClass = EnumHelper.get(name);
                if (enumClass == null) {
                    break;
                }

                temp = index + 1;
            } else if (!Character.isAlphabetic(curChar) && !Character.isDigit(curChar) && curChar != '_') {
                break;
            }
            ++index;
        }
        if (enumClass == null) {
            return -1;
        }

        if (temp < index) {
            String name = str.substring(temp, index);
            for (Object tso : enumClass.getEnumConstants()) {
                Enum<?> ts = (Enum<?>) tso;
                if (ts.name().equalsIgnoreCase(name)) {
                    this.value = ts;
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public Class<?> getReturnClass() {
        return this.value.getClass();
    }
}
