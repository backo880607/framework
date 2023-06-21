package com.pisces.framework.core;

import com.pisces.framework.core.config.BaseConfiguration;
import com.pisces.framework.core.entity.EnumHelper;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * 初始化器
 *
 * @author jason
 * @date 2022/12/07
 */
public class Initializer {
    private static final String DLL_CORE = "core";
    private static final String DLL_NAME = "primary";
    private static final String OS = System.getProperty("os.name").toLowerCase();

    protected Initializer() {
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOs() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public static boolean isMacOsx() {
        return OS.contains("mac") && OS.contains("os") && OS.contains("x");
    }

    public static native void load(List<Class<?>> classes);

    public static void execute() {
        EnumHelper.init();
        ObjectUtils.init();
        initModel();
    }

    private static void initModel() {
        Map<String, BaseConfiguration> modelConfigurations = AppUtils.getBeansOfType(BaseConfiguration.class);
        for (Map.Entry<String, BaseConfiguration> entry : modelConfigurations.entrySet()) {
            entry.getValue().init();
        }
    }
}
