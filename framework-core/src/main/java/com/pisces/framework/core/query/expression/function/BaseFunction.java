package com.pisces.framework.core.query.expression.function;

import com.pisces.framework.core.utils.lang.ClassUtils;
import com.pisces.framework.core.utils.lang.DateUtils;
import com.pisces.framework.core.utils.lang.DoubleUtils;
import com.pisces.framework.type.Duration;
import com.pisces.framework.type.MultiEnum;
import com.pisces.framework.type.PROPERTY_TYPE;
import org.springframework.expression.ExpressionException;

import java.util.Date;

/**
 * 基函数
 *
 * @author jason
 * @date 2022/12/07
 */
public class BaseFunction {

    protected BaseFunction() {
    }

    public static PROPERTY_TYPE getType(Object arg) {
        return ClassUtils.getPropertyType(arg.getClass());
    }

    public static Class<?> plusClass(Class<?> arg1, Class<?> arg2) {
        switch (ClassUtils.getPropertyType(arg1)) {
            case LONG:
                return longPlusClass(arg2);
            case DOUBLE:
                return doublePlusClass(arg2);
            case DATE_TIME:
                return dateTimePlusClass(arg2);
            case DURATION:
                return durationPlusClass(arg2);
            case STRING:
                return stringPlusClass(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> longPlusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
                return Long.class;
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> doublePlusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> dateTimePlusClass(Class<?> arg) {
        if (ClassUtils.getPropertyType(arg) == PROPERTY_TYPE.DURATION) {
            return Date.class;
        }
        throw new ExpressionException("");
    }

    private static Class<?> durationPlusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case DURATION:
                return Duration.class;
            case DATE_TIME:
                return Date.class;
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static Class<?> stringPlusClass(Class<?> arg) {
        if (ClassUtils.getPropertyType(arg) == PROPERTY_TYPE.STRING) {
            return String.class;
        }
        throw new ExpressionException("");
    }

    public static Object plus(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longPlus((Long) arg1, arg2);
            case DOUBLE:
                return doublePlus((Double) arg1, arg2);
            case DATE_TIME:
                return dateTimePlus((Date) arg1, arg2);
            case DURATION:
                return durationPlus((Duration) arg1, arg2);
            case STRING:
                return stringPlus((String) arg1, arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Object longPlus(Long arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 + (Long) arg2;
            case DOUBLE:
                return arg1 + (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Double doublePlus(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 + (Long) arg2;
            case DOUBLE:
                return arg1 + (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Date dateTimePlus(Date arg1, Object arg2) {
        if (getType(arg2) == PROPERTY_TYPE.DURATION) {
            return new Date(arg1.getTime() + ((Duration) arg2).getTime() * DateUtils.PER_SECOND);
        }
        throw new ExpressionException("");
    }

    private static Object durationPlus(Duration arg1, Object arg2) {
        switch (getType(arg2)) {
            case DURATION:
                return new Duration(arg1.getTime() + ((Duration) arg2).getTime());
            case DATE_TIME:
                return new Date(arg1.getTime() * DateUtils.PER_SECOND + ((Date) arg2).getTime());
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static String stringPlus(String arg1, Object arg2) {
        if (getType(arg2) == PROPERTY_TYPE.STRING) {
            return arg1 + (String) arg2;
        }
        throw new ExpressionException("");
    }

    public static Class<?> minusClass(Class<?> arg1, Class<?> arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longMinusClass(arg2);
            case DOUBLE:
                return doubleMinusClass(arg2);
            case DATE_TIME:
                return dateTimeMinusClass(arg2);
            case DURATION:
                return durationMinusClass(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> longMinusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
                return Long.class;
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> doubleMinusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> dateTimeMinusClass(Class<?> arg) {
        if (ClassUtils.getPropertyType(arg) == PROPERTY_TYPE.DURATION) {
            return Date.class;
        }
        throw new ExpressionException("");
    }

    private static Class<?> durationMinusClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case DURATION:
                return Duration.class;
            case DATE_TIME:
                return Date.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Object minus(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longMinus((Long) arg1, arg2);
            case DOUBLE:
                return doubleMinus((Double) arg1, arg2);
            case DATE_TIME:
                return dateTimeMinus((Date) arg1, arg2);
            case DURATION:
                return durationMinus((Duration) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static Object longMinus(Long arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 - (Long) arg2;
            case DOUBLE:
                return arg1 - (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Double doubleMinus(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 - (Long) arg2;
            case DOUBLE:
                return arg1 - (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Object dateTimeMinus(Date arg1, Object arg2) {
        if (getType(arg2) == PROPERTY_TYPE.DURATION) {
            return new Date(arg1.getTime() - ((Duration) arg2).getTime() * DateUtils.PER_SECOND);
        }
        throw new ExpressionException("");
    }

    private static Object durationMinus(Duration arg1, Object arg2) {
        if (getType(arg2) == PROPERTY_TYPE.DURATION) {
            return new Duration(arg1.getTime() - ((Duration) arg2).getTime());
        }
        throw new ExpressionException("");
    }

    public static Class<?> multiplyClass(Class<?> arg1, Class<?> arg2) {
        switch (ClassUtils.getPropertyType(arg1)) {
            case LONG:
                return longMultiplyClass(arg2);
            case DOUBLE:
                return doubleMultiplyClass(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> longMultiplyClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
                return Long.class;
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> doubleMultiplyClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Object multiply(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longMultiply((Long) arg1, arg2);
            case DOUBLE:
                return doubleMultiply((Double) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static Object longMultiply(Long arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 * (Long) arg2;
            case DOUBLE:
                return arg1 * (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Object doubleMultiply(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 * (Long) arg2;
            case DOUBLE:
                return arg1 * (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Class<?> divisionClass(Class<?> arg1, Class<?> arg2) {
        switch (ClassUtils.getPropertyType(arg1)) {
            case LONG:
                return longDivisionClass(arg2);
            case DOUBLE:
                return doubleDivisionClass(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> longDivisionClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
                return Long.class;
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> doubleDivisionClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
                return Double.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Object division(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longDivision((Long) arg1, arg2);
            case DOUBLE:
                return doubleDivision((Double) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static Object longDivision(Long arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 / (Long) arg2;
            case DOUBLE:
                return arg1 / (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static double doubleDivision(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 / (Long) arg2;
            case DOUBLE:
                return arg1 / (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Object mod(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case LONG:
                return longMod((Long) arg1, arg2);
            case DOUBLE:
                return doubleMod((Double) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static Object longMod(Long arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 % (Long) arg2;
            case DOUBLE:
                return arg1 % (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static double doubleMod(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1 % (Long) arg2;
            case DOUBLE:
                return arg1 % (Double) arg2;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Class<?> greaterClass(Class<?> arg1, Class<?> arg2) {
        switch (ClassUtils.getPropertyType(arg1)) {
            case LONG:
                return longGreaterClass(arg2);
            case DOUBLE:
                return doubleGreaterClass(arg2);
            case DATE_TIME:
                return dateTimeGreaterClass(arg2);
            case DURATION:
                return durationGreaterClass(arg2);
            case ENUM:
                return enumGreaterClass(arg2);
            case STRING:
                return stringGreaterClass(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> longGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case SHORT:
            case INTEGER:
            case LONG:
            case DOUBLE:
            case DURATION:
            case ENUM:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> doubleGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
            case DURATION:
            case ENUM:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> dateTimeGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case DATE_TIME:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> durationGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
            case DURATION:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> enumGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
            case ENUM:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static Class<?> stringGreaterClass(Class<?> arg) {
        switch (ClassUtils.getPropertyType(arg)) {
            case LONG:
            case DOUBLE:
            case DATE_TIME:
            case DURATION:
            case ENUM:
            case STRING:
                return Boolean.class;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static boolean greater(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case SHORT:
                return longGreater((Short) arg1, arg2);
            case INTEGER:
                return longGreater((Integer) arg1, arg2);
            case LONG:
                return longGreater((Long) arg1, arg2);
            case DOUBLE:
                return doubleGreater((Double) arg1, arg2);
            case DATE_TIME:
                return dateTimeGreater((Date) arg1, arg2);
            case DURATION:
                return durationGreater((Duration) arg1, arg2);
            case ENUM:
                return enumGreater((Enum<?>) arg1, arg2);
            case STRING:
                return stringGreater((String) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static boolean longGreater(long arg1, Object arg2) {
        switch (getType(arg2)) {
            case SHORT:
                return arg1 > (Short) arg2;
            case INTEGER:
                return arg1 > (Integer) arg2;
            case LONG:
                return arg1 > (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isLess((Double) arg2, arg1);
            case DURATION:
                return arg1 > ((Duration) arg2).getTime();
            case ENUM:
                return arg1 > ((Enum<?>) arg2).ordinal();
            case STRING:
                return arg1 > Long.parseLong((String) arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean doubleGreater(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return DoubleUtils.isLess((Long) arg2, arg1);
            case DOUBLE:
                return DoubleUtils.isLess((Double) arg2, arg1);
            case DURATION:
                return DoubleUtils.isGreater(arg1, ((Duration) arg2).getTime());
            case ENUM:
                return DoubleUtils.isGreater(arg1, ((Enum<?>) arg2).ordinal());
            case STRING:
                return DoubleUtils.isLess(Double.parseDouble((String) arg2), arg1);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean dateTimeGreater(Date arg1, Object arg2) {
        switch (getType(arg2)) {
            case DATE_TIME:
                return arg1.getTime() > ((Date) arg2).getTime();
            case STRING:
                Date temp = DateUtils.parse((String) arg2);
                return arg1.getTime() > temp.getTime();
        }
        throw new ExpressionException("");
    }

    private static boolean durationGreater(Duration dur, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return dur.getTime() > (long) arg2;
            case DOUBLE:
                return DoubleUtils.isGreater(dur.getTime(), (double) arg2);
            case DURATION:
                return dur.getTime() > ((Duration) arg2).getTime();
            case STRING:
                return dur.getTime() > new Duration((String) arg2).getTime();
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean enumGreater(Enum<?> arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1.ordinal() > (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isGreater(arg1.ordinal(), (double) arg2);
            case ENUM:
                return arg1.ordinal() > ((Enum<?>) arg2).ordinal();
            case STRING:
                Enum<?> target = Enum.valueOf(arg1.getClass(), (String) arg2);
                return arg1.ordinal() > target.ordinal();
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean stringGreater(String arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return Long.parseLong(arg1) > (long) arg2;
            case DOUBLE:
                return DoubleUtils.isGreater(Double.parseDouble(arg1), (double) arg2);
            case DATE_TIME:
                break;
            case DURATION:
                return new Duration(arg1).getTime() > ((Duration) arg2).getTime();
            case ENUM:
                break;
            case STRING:
                return arg1.compareTo((String) arg2) > 0;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Class<?> greaterEqualClass(Class<?> arg1, Class<?> arg2) {
        return greaterClass(arg1, arg2);
    }

    public static boolean greaterEqual(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case SHORT:
                return longGreaterEqual((Short) arg1, arg2);
            case INTEGER:
                return longGreaterEqual((Integer) arg1, arg2);
            case LONG:
                return longGreaterEqual((Long) arg1, arg2);
            case DOUBLE:
                return doubleGreaterEqual((Double) arg1, arg2);
            case DATE_TIME:
                return dateTimeGreaterEqual((Date) arg1, arg2);
            case ENUM:
                return enumGreaterEqual((Enum<?>) arg1, arg2);
            case DURATION:
                return durationGreaterEqual((Duration) arg1, arg2);
            case STRING:
                return stringGreaterEqual((String) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static boolean longGreaterEqual(long arg1, Object arg2) {
        switch (getType(arg2)) {
            case SHORT:
                return arg1 >= (Short) arg2;
            case INTEGER:
                return arg1 >= (Integer) arg2;
            case LONG:
                return arg1 >= (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isLessEqual((Double) arg2, arg1);
            case STRING:
                return arg1 >= Long.parseLong((String) arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean doubleGreaterEqual(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return DoubleUtils.isLessEqual((Long) arg2, arg1);
            case DOUBLE:
                return DoubleUtils.isLessEqual((Double) arg2, arg1);
            case STRING:
                return DoubleUtils.isLessEqual(Double.parseDouble((String) arg2), arg1);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean dateTimeGreaterEqual(Date arg1, Object arg2) {
        switch (getType(arg2)) {
            case DATE_TIME:
                return arg1.getTime() >= ((Date) arg2).getTime();
            case STRING:
                Date temp = DateUtils.parse((String) arg2);
                return arg1.getTime() >= temp.getTime();
        }
        throw new ExpressionException("");
    }

    private static boolean enumGreaterEqual(Enum<?> arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1.ordinal() >= (Long) arg2;
            case ENUM:
                return arg1.ordinal() >= ((Enum<?>) arg2).ordinal();
            case STRING:
                Enum<?> target = Enum.valueOf(arg1.getClass(), (String) arg2);
                return arg1.ordinal() >= target.ordinal();
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean durationGreaterEqual(Duration arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1.getTime() >= (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isLessEqual((double) arg2, arg1.getTime());
            case DURATION:
                return arg1.getTime() > ((Duration) arg2).getTime();
            case STRING:
                return arg1.getTime() >= new Duration(arg2.toString()).getTime();
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean stringGreaterEqual(String arg1, Object arg2) {
        if (getType(arg2) == PROPERTY_TYPE.STRING) {
            return arg1.compareTo((String) arg2) >= 0;
        }
        throw new ExpressionException("");
    }

    public static Class<?> lessClass(Class<?> arg1, Class<?> arg2) {
        return greaterClass(arg1, arg2);
    }

    public static boolean less(Object arg1, Object arg2) {
        return !greaterEqual(arg1, arg2);
    }

    public static Class<?> lessEqualClass(Class<?> arg1, Class<?> arg2) {
        return greaterClass(arg1, arg2);
    }

    public static boolean lessEqual(Object arg1, Object arg2) {
        return !greater(arg1, arg2);
    }

    public static Class<?> equalClass(Class<?> arg1, Class<?> arg2) {
        return greaterClass(arg1, arg2);
    }

    public static boolean equal(Object arg1, Object arg2) {
        switch (getType(arg1)) {
            case SHORT:
                return lessEqual((Short) arg1, arg2);
            case INTEGER:
                return longEqual((Integer) arg1, arg2);
            case LONG:
                return longEqual((Long) arg1, arg2);
            case DOUBLE:
                return doubleEqual((Double) arg1, arg2);
            case DATE_TIME:
                return dateTimeEqual((Date) arg1, arg2);
            case ENUM:
                return enumEqual((Enum<?>) arg1, arg2);
            case DURATION:
                return durationEqual((Duration) arg1, arg2);
            case STRING:
                return stringEqual((String) arg1, arg2);
            default:
                break;
        }

        throw new ExpressionException("");
    }

    private static boolean longEqual(long arg1, Object arg2) {
        switch (getType(arg2)) {
            case SHORT:
                return arg1 == (Short) arg2;
            case INTEGER:
                return arg1 == (Integer) arg2;
            case LONG:
                return arg1 == (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isEqual(arg1, (Double) arg2);
            case DURATION:
                return arg1 == ((Duration) arg2).getTime();
            case ENUM:
                return arg1 == ((Enum<?>) arg2).ordinal();
            case MULTI_ENUM:
                return arg1 == ((MultiEnum<?>) arg2).getValue();
            case STRING:
                return arg1 == Long.parseLong((String) arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean doubleEqual(Double arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return DoubleUtils.isEqual(arg1, (Long) arg2);
            case DOUBLE:
                return DoubleUtils.isEqual(arg1, (Double) arg2);
            case DURATION:
                return DoubleUtils.isEqual(arg1, ((Duration) arg2).getTime());
            case ENUM:
                return DoubleUtils.isEqual(arg1, ((Enum<?>) arg2).ordinal());
            case MULTI_ENUM:
                return DoubleUtils.isEqual(arg1, ((MultiEnum<?>) arg2).getValue());
            case STRING:
                return DoubleUtils.isEqual(arg1, Double.parseDouble((String) arg2));
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean dateTimeEqual(Date arg1, Object arg2) {
        switch (getType(arg2)) {
            case DATE_TIME:
                return arg1.getTime() == ((Date) arg2).getTime();
            case STRING:
                Date temp = DateUtils.parse((String) arg2);
                return arg1.getTime() == temp.getTime();
        }
        throw new ExpressionException("");
    }

    private static boolean enumEqual(Enum<?> arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1.ordinal() == (Long) arg2;
            case DOUBLE:
                return DoubleUtils.isEqual(arg1.ordinal(), (double) arg2);
            case ENUM:
                return arg1.ordinal() == ((Enum<?>) arg2).ordinal();
            case STRING:
                Enum<?> target = Enum.valueOf(arg1.getClass(), (String) arg2);
                return arg1.ordinal() == target.ordinal();
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean durationEqual(Duration arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return arg1.getTime() == (long) arg2;
            case DOUBLE:
                return DoubleUtils.isEqual(arg1.getTime(), (double) arg2);
            case DURATION:
                return arg1.getTime() == ((Duration) arg2).getTime();
            case STRING:
                return arg1.toString().equals(arg2);
            default:
                break;
        }
        throw new ExpressionException("");
    }

    private static boolean stringEqual(String arg1, Object arg2) {
        switch (getType(arg2)) {
            case LONG:
                return Long.parseLong(arg1) == (long) arg2;
            case DOUBLE:
                return DoubleUtils.isEqual(Double.parseDouble(arg1), (double) arg2);
            case ENUM:
                break;
            case STRING:
                return arg1.compareTo((String) arg2) == 0;
            default:
                break;
        }
        throw new ExpressionException("");
    }

    public static Class<?> notEqualClass(Class<?> arg1, Class<?> arg2) {
        return equalClass(arg1, arg2);
    }

    public static boolean notEqual(Object arg1, Object arg2) {
        return !equal(arg1, arg2);
    }

    public static Class<?> notClass(Class<?> arg1, Class<?> arg2) {
        if (ClassUtils.getPropertyType(arg1) == PROPERTY_TYPE.BOOLEAN) {
            return Boolean.class;
        }
        throw new ExpressionException("");
    }

    public static Object not(Object arg1, Object arg2) {
        return !(boolean) arg1;
    }

    public static Class<?> andClass(Class<?> arg1, Class<?> arg2) {
        if (ClassUtils.getPropertyType(arg1) == PROPERTY_TYPE.BOOLEAN) {
            return boolAndClass(arg2);
        }
        throw new ExpressionException("");
    }

    private static Class<?> boolAndClass(Class<?> arg) {
        if (ClassUtils.getPropertyType(arg) == PROPERTY_TYPE.BOOLEAN) {
            return Boolean.class;
        }
        throw new ExpressionException("");
    }

    public static Object and(Object arg1, Object arg2) {
        return (boolean) arg1 && (boolean) arg2;
    }

    public static Class<?> orClass(Class<?> arg1, Class<?> arg2) {
        if (ClassUtils.getPropertyType(arg1) == PROPERTY_TYPE.BOOLEAN) {
            return boolOrClass(arg2);
        }
        throw new ExpressionException("");
    }

    private static Class<?> boolOrClass(Class<?> arg) {
        if (ClassUtils.getPropertyType(arg) == PROPERTY_TYPE.BOOLEAN) {
            return Boolean.class;
        }
        throw new ExpressionException("");
    }

    public static Object or(Object arg1, Object arg2) {
        return (boolean) arg1 || (boolean) arg2;
    }
}
