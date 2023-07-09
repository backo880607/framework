package com.pisces.framework.web.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * 字符串长
 *
 * @author jason
 * @date 2022/12/08
 */
public class StringToLong implements Converter<String, Long> {

    @Override
    public Long convert(String source) {
        if (source.isEmpty()) {
            return null;
        }

        String trimmed = StringUtils.trimAllWhitespace(source);
        if (!trimmed.isEmpty()) {
            final String semiSplit = "\"";
            if (trimmed.startsWith(semiSplit) && trimmed.endsWith(semiSplit)) {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
            }
        }
        return NumberUtils.parseNumber(trimmed, Long.class);
    }

}
