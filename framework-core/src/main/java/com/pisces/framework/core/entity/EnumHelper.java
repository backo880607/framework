package com.pisces.framework.core.entity;

import com.pisces.framework.core.config.BaseProperties;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.io.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * enum助手
 *
 * @author jason
 * @date 2022/12/07
 */
public final class EnumHelper {
    private static final Map<String, Class<?>> VALUES = new HashMap<>();
    private static final String PACKAGE_SPLIT = ";";

    private EnumHelper() {
    }

    public static void init() {
        Map<String, BaseProperties> configs = AppUtils.getBeansOfType(BaseProperties.class);
        for (Entry<String, BaseProperties> entry : configs.entrySet()) {
            BaseProperties property = entry.getValue();
            if (property.getEnumPackage() != null) {
                for (String enumPath : property.getEnumPackage().split(PACKAGE_SPLIT)) {
                    if (enumPath.isEmpty()) {
                        continue;
                    }

                    register(enumPath);
                }
            }
        }
    }

    public static void register(String packPath) {
        for (Class<?> cls : FileUtils.loadClass(packPath)) {
            if (Enum.class.isAssignableFrom(cls)) {
                VALUES.put(cls.getSimpleName(), cls);
            }
        }
    }

    public static Class<?> get(String name) {
        return VALUES.get(name);
    }
}
