package com.pisces.framework.core.utils;

import com.pisces.framework.core.Initializer;
import com.pisces.framework.core.config.BaseConfiguration;
import com.pisces.framework.core.entity.AccountData;
import com.pisces.framework.core.utils.lang.CollectionUtils;
import com.pisces.framework.core.utils.lang.Guard;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 应用跑龙套
 *
 * @author jason
 * @date 2022/12/07
 */
@Getter
public final class AppUtils {
    public static final long ROOT_TENANT = 7;
    private static ApplicationContext context;
    private static final ThreadLocal<AccountData> CUR_ACCOUNT_DATA = new ThreadLocal<>();

    private AppUtils() {
    }

    public static void setContext(ApplicationContext context) {
        AppUtils.context = context;
        Initializer.execute();
    }

    public static String getProperty(String name) {
        return context.getEnvironment().getProperty(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return context.getBean(requiredType);
    }

    public static <T> T getBean(String name) {
        return (T) context.getBean(name);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    public static String getAccount() {
        final AccountData userData = CUR_ACCOUNT_DATA.get();
        return userData == null ? "system" : userData.getAccount();
    }

    public static long getTenant() {
        final AccountData userData = CUR_ACCOUNT_DATA.get();
        return userData == null ? 0L : Guard.value(userData.getTenant());
    }

    public static long getDataSet() {
        final AccountData userData = CUR_ACCOUNT_DATA.get();
        return userData == null ? 0 : Guard.value(userData.getDataSet());
    }

    public static List<String> getAuthorities() {
        final AccountData userData = CUR_ACCOUNT_DATA.get();
        return userData == null || CollectionUtils.isEmpty(userData.getAuthorities()) ? new ArrayList<>() : userData.getAuthorities();
    }

    public static void bindAccount(AccountData data) {
        CUR_ACCOUNT_DATA.set(data);
        Map<String, BaseConfiguration> modelConfigurations = AppUtils.getBeansOfType(BaseConfiguration.class);
        for (Map.Entry<String, BaseConfiguration> entry : modelConfigurations.entrySet()) {
            entry.getValue().bindAccount(data);
        }
    }

    public static void unbindAccount() {
        CUR_ACCOUNT_DATA.remove();
    }
}
