package com.pisces.framework.core.locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息
 *
 * @author jason
 * @date 2022/12/07
 */
public class Message {
    private final Logger log;

    public Message(Class<?> arg) {
        log = LoggerFactory.getLogger(arg);
    }

    private String getMessage(Enum<?> key, Object... arguments) {
        return LocaleManager.getLanguage(key, arguments);
    }

    public void debug(Enum<?> key, Object... arguments) {
        log.debug(getMessage(key, arguments));
    }

    public void info(Enum<?> key, Object... arguments) {
        log.info(getMessage(key, arguments));
    }

    public void warn(Enum<?> key, Object... arguments) {
        log.warn(getMessage(key, arguments));
    }

    public void error(Enum<?> key, Object... arguments) {
        log.error(getMessage(key, arguments));
    }
}
