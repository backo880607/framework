package com.pisces.framework.core.utils.lang;

import com.pisces.framework.core.exception.SystemException;
import com.pisces.framework.type.Duration;

import java.util.Date;

/**
 * 警卫
 *
 * @author jason
 * @date 2022/12/07
 */
public final class Guard {

    private Guard() {
    }

    public static byte value(Byte arg) {
        return arg != null ? arg : 0;
    }

    public static byte value(Byte arg, byte def) {
        return arg != null ? arg : def;
    }

    public static boolean value(Boolean arg) {
        return arg != null ? arg : false;
    }

    public static boolean value(Boolean arg, boolean def) {
        return arg != null ? arg : def;
    }

    public static short value(Short arg) {
        return arg != null ? arg : 0;
    }

    public static short value(Short arg, short def) {
        return arg != null ? arg : def;
    }

    public static int value(Integer arg) {
        return arg != null ? arg : 0;
    }

    public static int value(Integer arg, int def) {
        return arg != null ? arg : def;
    }

    public static long value(Long arg) {
        return arg != null ? arg : 0;
    }

    public static long value(Long arg, long def) {
        return arg != null ? arg : def;
    }

    public static float value(Float arg) {
        return arg != null ? arg : 0.0f;
    }

    public static float value(Float arg, float def) {
        return arg != null ? arg : def;
    }

    public static double value(Double arg) {
        return arg != null ? arg : 0.0f;
    }

    public static double value(Double arg, double def) {
        return arg != null ? arg : def;
    }

    public static char value(Character arg) {
        return arg != null ? arg : 0;
    }

    public static char value(Character arg, char def) {
        return arg != null ? arg : def;
    }

    public static String value(String arg) {
        return arg != null ? arg : "";
    }

    public static String value(String arg, String def) {
        return arg != null ? arg : def;
    }

    public static Date value(Date arg) {
        return arg != null ? arg : DateUtils.INVALID;
    }

    public static Date value(Date arg, Date def) {
        return arg != null ? arg : def;
    }

    public static long valueTime(Date arg) {
        return arg != null ? arg.getTime() : DateUtils.INVALID.getTime();
    }

    public static long valueTime(Date arg, long def) {
        return arg != null ? arg.getTime() : def;
    }

    public static Object value(Object arg, Class<?> cls) {
        if (arg != null) {
            return arg;
        }

        if (cls == Integer.class) {
            return (Integer) 0;
        } else if (cls == Long.class) {
            return (Long) 0L;
        } else if (cls == Float.class) {
            return (Float) 0.0f;
        } else if (cls == Double.class) {
            return (Double) 0.0d;
        } else if (cls == String.class) {
            return "";
        } else if (cls == Date.class) {
            return DateUtils.INVALID;
        } else if (cls == Short.class) {
            Short result = 0;
            return result;
        } else if (cls == Byte.class) {
            Byte result = 0;
            return result;
        } else if (cls == Character.class) {
            return Character.valueOf((char) 0);
        } else if (cls == Duration.class) {
            return Duration.INVALID;
        }

        return null;
    }

    /**
     * 断言 condition 必须为 true
     *
     * @param condition 条件
     * @param msg       消息
     * @param params    消息参数
     */
    public static void assertTrue(boolean condition, String msg, Object... params) {
        if (!condition) {
            throw new SystemException(msg, params);
        }
    }


    /**
     * 断言传入的内容不能为 null
     */
    public static void assertNotNull(Object object, String msg, Object params) {
        assertTrue(object != null, msg, params);
    }


    /**
     * 断言传入的数组内容不能为 null 或者 空
     */
    public static <T> void assertAreNotNull(T[] elements, String msg, Object params) {
        if (elements == null || elements.length == 0) {
            throw new SystemException(msg, params);
        }
        for (T element : elements) {
            if (element == null) {
                throw new SystemException(msg, params);
            }
        }
    }
}
