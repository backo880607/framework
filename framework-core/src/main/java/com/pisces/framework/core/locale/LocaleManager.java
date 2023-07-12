package com.pisces.framework.core.locale;

import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 地区经理
 *
 * @author jason
 * @date 2022/12/07
 */
public class LocaleManager {
    private static volatile LanguageService languageService;

    protected LocaleManager() {
    }

    private static LanguageService getLanguageService() {
        if (languageService == null) {
            synchronized (LocaleManager.class) {
                if (languageService == null) {
                    Map<String, LanguageService> mgr = AppUtils.getBeansOfType(LanguageService.class);
                    for (Entry<String, LanguageService> entry : mgr.entrySet()) {
                        languageService = entry.getValue();
                        break;
                    }
                }
            }
        }

        return languageService;
    }

    public static String getLanguage(Enum<?> key, Object... arguments) {
        if (getLanguageService() == null) {
            return "";
        }

        return getLanguageService().get(key, arguments);
    }

    public static String getLanguage(Class<?> clazz, Enum<?> key, Object... arguments) {
        if (getLanguageService() == null) {
            return "";
        }

        return getLanguageService().get(clazz, key, arguments);
    }

    /**
     * 获得语言
     *
     * @param clazz clazz
     * @param field 场
     * @return {@link String}
     */
    public static String getLanguage(Class<?> clazz, String field) {
        if (getLanguageService() == null) {
            return "";
        }

        return getLanguageService().get(clazz, field);
    }

    public static Locale getLocale() {
        if (getLanguageService() != null) {
            return getLanguageService().getLocale();
        }

        return Locale.getDefault();
    }

    public static void setLocale(Locale locale) {
        if (getLanguageService() == null || locale == null) {
            return;
        }
        getLanguageService().setLocale(locale);
    }

    public static void setLocale(String locale) {
        setLocale(parseLocale(locale));
    }

    public static Locale parseLocale(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        String[] strLocale = text.split("_");
        String language = strLocale[0];
        String country = strLocale.length > 1 ? strLocale[1] : "";
        String variant = strLocale.length > 2 ? strLocale[2] : "";
        return new Locale(language, country, variant);
    }
}
