package com.pisces.framework.core.utils.lang;


import com.pisces.framework.core.entity.Duration;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期工具类
 *
 * @author jason
 * @date 2022/12/07
 */
public final class DateUtils {
    public static final Date MIN;
    public static final Date MAX = new Date(Long.MAX_VALUE);
    public static final long PER_SECOND = 1000;
    public static final long PER_MINUTE = 60000;
    public static final long PER_HOUR = 3600000;
    public static final long PER_DAY = 86400000;
    public static final Date INVALID = new Date();
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATE_MINUTE_PATTERN2 = "yyyy-MM-dd'T'HH:mm";
    public static final String DATE_MILLISECOND_PATTERN = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String DATE_CST_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String DATE_PATTERN_WITHOUT_DIVIDING = "yyyyMMdd";
    private static final long ZONE_TIME;

    private static final ThreadLocal<Map<String, SimpleDateFormat>> TL = ThreadLocal.withInitial(HashMap::new);
    private static final Map<String, DateTimeFormatter> dateTimeFormatters = new ConcurrentHashMap<>();

    static {
        Calendar cal = Calendar.getInstance();
        ZONE_TIME = cal.get(Calendar.ZONE_OFFSET);
        cal.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        MIN = cal.getTime();
    }

    private DateUtils() {
    }

    private static final ThreadLocal<Calendar> CALENDAR_LOCAL = new ThreadLocal<>();

    private static Calendar getCalendar(Date date) {
        Calendar calendar = CALENDAR_LOCAL.get();
        if (calendar == null) {
            calendar = Calendar.getInstance();
            CALENDAR_LOCAL.set(calendar);
        }
        calendar.setTime(date);
        return calendar;
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        DateTimeFormatter ret = dateTimeFormatters.get(pattern);
        if (ret == null) {
            ret = DateTimeFormatter.ofPattern(pattern);
            dateTimeFormatters.put(pattern, ret);
        }
        return ret;
    }

    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        SimpleDateFormat ret = TL.get().get(pattern);
        if (ret == null) {
            if (DATE_CST_PATTERN.equals(pattern)) {
                ret = new SimpleDateFormat(DATE_CST_PATTERN, Locale.US);
            } else {
                ret = new SimpleDateFormat(pattern);
            }
            TL.get().put(pattern, ret);
        }
        return ret;
    }

    public static boolean isValid(Date date) {
        return date != null && date != DateUtils.INVALID;
    }

    public static String format(Date date, String format) {
        if (date == null || date == DateUtils.INVALID) {
            return "";
        }
        return getSimpleDateFormat(format).format(date);
    }

    public static String format(Date date) {
        return format(date, DATE_TIME_PATTERN);
    }

    public static Date parse(Object value) {
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        }
        if (value instanceof LocalDate) {
            return DateUtils.toDate((LocalDate) value);
        }
        if (value instanceof LocalDateTime) {
            return DateUtils.toDate((LocalDateTime) value);
        }
        if (value instanceof LocalTime) {
            return DateUtils.toDate((LocalTime) value);
        }
        String s = value.toString();
        if (StringUtils.isNumber(s)) {
            return new Date(Long.parseLong(s));
        }
        return DateUtils.parse(s);
    }

    public static Date parseDate(Object value) {
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        if (value instanceof Timestamp) {
            return new Date(((Timestamp) value).getTime());
        }
        if (value instanceof LocalDate) {
            return DateUtils.toDate((LocalDate) value);
        }
        if (value instanceof LocalDateTime) {
            return DateUtils.toDate((LocalDateTime) value);
        }
        if (value instanceof LocalTime) {
            return DateUtils.toDate((LocalTime) value);
        }
        String s = value.toString();
        if (StringUtils.isNumber(s)) {
            return new Date(Long.parseLong(s));
        }
        return DateUtils.parseDate(s);
    }

    public static Date parseDate(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        dateString = dateString.trim();
        try {
            SimpleDateFormat sdf = getSimpleDateFormat(getPattern(dateString));
            try {
                return sdf.parse(dateString);
            } catch (ParseException ex) {
                //2022-10-23 00:00:00.0
                int lastIndexOf = dateString.lastIndexOf(".");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                //2022-10-23 00:00:00,0
                lastIndexOf = dateString.lastIndexOf(",");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                //2022-10-23 00:00:00 000123
                lastIndexOf = dateString.lastIndexOf(" ");
                if (lastIndexOf == 19) {
                    return parseDate(dateString.substring(0, lastIndexOf));
                }

                if (dateString.contains(".") || dateString.contains("/")) {
                    dateString = dateString.replace(".", "-").replace("/", "-");
                    return sdf.parse(dateString);
                } else {
                    throw ex;
                }
            }
        } catch (ParseException ex) {
            throw new IllegalArgumentException("The date format is not supported for the date string: " + dateString);
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        dateString = dateString.trim();
        DateTimeFormatter dateTimeFormatter = getDateTimeFormatter(getPattern(dateString));
        try {
            return LocalDateTime.parse(dateString, dateTimeFormatter);
        } catch (Exception ex) {
            //2022-10-23 00:00:00.0
            int lastIndexOf = dateString.lastIndexOf(".");
            if (lastIndexOf == 19) {
                return parseLocalDateTime(dateString.substring(0, lastIndexOf));
            }

            //2022-10-23 00:00:00,0
            lastIndexOf = dateString.lastIndexOf(",");
            if (lastIndexOf == 19) {
                return parseLocalDateTime(dateString.substring(0, lastIndexOf));
            }

            //2022-10-23 00:00:00 000123
            lastIndexOf = dateString.lastIndexOf(" ");
            if (lastIndexOf == 19) {
                return parseLocalDateTime(dateString.substring(0, lastIndexOf));
            }

            if (dateString.contains(".") || dateString.contains("/")) {
                dateString = dateString.replace(".", "-").replace("/", "-");
                dateTimeFormatter = getDateTimeFormatter(getPattern(dateString));
                return LocalDateTime.parse(dateString, dateTimeFormatter);
            } else {
                throw ex;
            }
        }
    }


    private static String getPattern(String dateString) {
        int length = dateString.length();
        if (length == DATE_TIME_PATTERN.length()) {
            return DATE_TIME_PATTERN;
        } else if (length == DATE_PATTERN.length()) {
            return DATE_PATTERN;
        } else if (length == DATE_MINUTE_PATTERN.length()) {
            if (dateString.contains("T")) {
                return DATE_MINUTE_PATTERN2;
            }
            return DATE_MINUTE_PATTERN;
        } else if (length == DATE_MILLISECOND_PATTERN.length()) {
            return DATE_MILLISECOND_PATTERN;
        } else if (length == DATE_PATTERN_WITHOUT_DIVIDING.length()) {
            return DATE_PATTERN_WITHOUT_DIVIDING;
        } else if (length == DATE_CST_PATTERN.length()) {
            return DATE_CST_PATTERN;
        } else {
            throw new IllegalArgumentException("The date format is not supported for the date string: " + dateString);
        }
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }


    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        // java.sql.Date 不支持 toInstant()，需要先转换成 java.util.Date
        if (date instanceof java.sql.Date) {
            date = new Date(date.getTime());
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalTime();
    }


    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }


    public static Date toDate(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }


    public static String toDateTimeString(Date date) {
        return toString(date, DATE_TIME_PATTERN);
    }


    public static String toString(Date date, String pattern) {
        return date == null ? null : getSimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String value, String format) throws ParseException {
        if (value == null || value.isEmpty()) {
            return DateUtils.INVALID;
        }
        return getSimpleDateFormat(format).parse(value);
    }

    public static Date now() {
        return new Date();
    }

    public static Date today() {
        return yearMonthDay(now());
    }

    public static Date nowTime() {
        return hourMinuteSecond(now());
    }

    public static int thisYear() {
        return year(now());
    }

    /**
     * 返回今天是周几
     * 返回值：1/2/3/4/5/6/7
     *
     * @param date 日期
     * @return int int
     */
    public static int dayOfWeek(Date date) {
        int week = getCalendar(date).get(Calendar.DAY_OF_WEEK);
        switch (week) {
            case 1:
                week = 7;
                break;
            case 2:
                week = 1;
                break;
            case 3:
                week = 2;
                break;
            case 4:
                week = 3;
                break;
            case 5:
                week = 4;
                break;
            case 6:
                week = 5;
                break;
            case 7:
                week = 6;
                break;
            default:
                break;
        }
        return week;
    }

    /**
     * 返回这一年的第几天
     *
     * @param date 日期
     * @return int int
     */
    public static int dayOfYear(Date date) {
        return getCalendar(date).get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 返回这个月的第几周  (1号开始 ,7天为一周)
     *
     * @param date 日期
     * @return int int
     */
    public static int dayOfWeekInMonth(Date date) {
        return getCalendar(date).get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 返回这个月的第几周 (周一开始,到周日为一周)
     *
     * @param date 日期
     * @return int int
     */
    public static int weekOfMonth(Date date) {
        return getCalendar(date).get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 返回这一年的第几周 (1月1日开始,7天为一周)
     *
     * @param date date
     * @return int
     */
    public static int weekOfYear(Date date) {
        return getCalendar(date).get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 返回上午还是下午
     *
     * @param date 日期
     * @return int
     */
    public static int amPm(Date date) {
        return getCalendar(date).get(Calendar.AM_PM);
    }

    /**
     * 返回这一天的第几个小时
     *
     * @param date 日期
     * @return int
     */
    public static int hourOfDay(Date date) {
        return getCalendar(date).get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回年份
     *
     * @param date 日期
     * @return int
     */
    public static int year(Date date) {
        return getCalendar(date).get(Calendar.YEAR);
    }

    /**
     * 返回月份
     *
     * @param date 日期
     * @return int
     */
    public static int month(Date date) {
        return getCalendar(date).get(Calendar.MONTH) + 1;
    }

    /**
     * 返回一个月中的第几天
     *
     * @param date 日期
     * @return int
     */
    public static int day(Date date) {
        return getCalendar(date).get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回小时
     *
     * @param date 日期
     * @return int
     */
    public static int hour(Date date) {
        return getCalendar(date).get(Calendar.HOUR);
    }

    /**
     * 返回分钟
     *
     * @param date 日期
     * @return int
     */
    public static int minute(Date date) {
        return getCalendar(date).get(Calendar.MINUTE);
    }

    /**
     * 返回秒数
     *
     * @param date 日期
     * @return int
     */
    public static int second(Date date) {
        return getCalendar(date).get(Calendar.SECOND);
    }

    /**
     * 返回毫秒
     *
     * @param date 日期
     * @return int
     */
    public static int millisecond(Date date) {
        return getCalendar(date).get(Calendar.MILLISECOND);
    }

    /**
     * 返回  年份+月份
     *
     * @param date 日期
     * @return int
     */
    public static Date yearMonth(Date date) {
        long result = ((date.getTime() + ZONE_TIME) / PER_DAY) * PER_DAY - ZONE_TIME;
        return new Date(result - (day(date) - 1) * PER_DAY);
    }

    /**
     * 返回 年份+月份+日期
     *
     * @param date 日期
     * @return Date
     */
    public static Date yearMonthDay(Date date) {
        return new Date(((date.getTime() + ZONE_TIME) / PER_DAY) * PER_DAY - ZONE_TIME);
    }

    /**
     * 返回  小时+分钟+秒数
     *
     * @param date 日期
     * @return Date
     */
    public static Date hourMinuteSecond(Date date) {
        long result = ((date.getTime() + ZONE_TIME) / PER_DAY) * PER_DAY - ZONE_TIME;
        return new Date(date.getTime() - result - ZONE_TIME);
    }

    /**
     * 返回两个日期的最小值
     *
     * @param lhs 日期
     * @param rhs 日期
     * @return Date
     */
    public static Date min(Date lhs, Date rhs) {
        if (lhs == null) {
            return rhs;
        }
        if (rhs == null) {
            return lhs;
        }
        return lhs.before(rhs) ? lhs : rhs;
    }

    /**
     * 返回两个日期的最大值
     *
     * @param lhs 日期
     * @param rhs 日期
     * @return Date
     */
    public static Date max(Date lhs, Date rhs) {
        if (lhs == null) {
            return rhs;
        }
        if (rhs == null) {
            return lhs;
        }
        return lhs.before(rhs) ? rhs : lhs;
    }

    public static Date add(Date date, Date time) {
        return new Date(date.getTime() + time.getTime() - ZONE_TIME);
    }

    public static Date add(Date date, Duration duration) {
        return new Date(date.getTime() + duration.getTimeInMillis());
    }

    public static Date sub(Date date, Duration duration) {
        return new Date(date.getTime() - duration.getTimeInMillis());
    }

    /**
     * 增加天数
     *
     * @param date  日期
     * @param value 正数增加，负数减少
     * @return Date
     */
    public static Date addDay(Date date, int value) {
        return new Date(date.getTime() + value * PER_DAY);
    }

    /**
     * 增加小时
     *
     * @param date  日期
     * @param value 正数增加，负数减少
     * @return Date
     */
    public static Date addHour(Date date, int value) {
        return new Date(date.getTime() + value * PER_HOUR);
    }

    /**
     * 增加分钟
     *
     * @param date  日期
     * @param value 正数增加，负数减少
     * @return Date
     */
    public static Date addMinute(Date date, int value) {
        return new Date(date.getTime() + value * PER_MINUTE);
    }

    /**
     * 增加秒数
     *
     * @param date  日期
     * @param value 正数增加，负数减少
     * @return Date
     */
    public static Date addSecond(Date date, int value) {
        return new Date(date.getTime() + value * PER_SECOND);
    }

    /**
     * 判断after相差before多少秒
     */
    public static long differentSeconds(Date before, Date after) {
        return (after.getTime() - before.getTime()) / PER_SECOND;
    }

    /**
     * 判断after相差before多少天
     */
    public static int differentDays(long before, long after) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(before);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(after);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {
            // 非同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                final boolean isLeapYear = i % 4 == 0 && i % 100 != 0 || i % 400 == 0;
                if (isLeapYear) {
                    // 闰年
                    timeDistance += 366;
                } else {
                    // 不是闰年
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        }
        return day2 - day1;
    }

    /**
     * 判断after相差before多少周
     */
    public static int differentWeeks(long before, long after) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(before);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(after);
        int week1 = cal1.get(Calendar.WEEK_OF_YEAR);
        int week2 = cal2.get(Calendar.WEEK_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        int weeks = week2 - week1;
        for (int i = 0; i < year2 - year1; i++) {
            weeks += cal1.getActualMaximum(Calendar.WEEK_OF_YEAR);
            cal1.add(Calendar.YEAR, 1);
        }
        return weeks;
    }

    /**
     * 判断after相差before多少月
     */
    public static int differentMonths(long before, long after) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(before);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(after);
        int month1 = cal1.get(Calendar.MONTH);
        int month2 = cal2.get(Calendar.MONTH);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return 12 * (year2 - year1) + (month2 - month1);
    }

    /**
     * 判断after相差before多少年
     */
    public static int differentYears(long before, long after) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(before);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(after);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        return (year2 - year1);
    }

    /**
     * 计算相对于dateToCompare的年龄，长用于计算指定生日在某年的年龄
     *
     * @param birthday      生日
     * @param dateToCompare 需要对比的日期
     * @return 年龄
     */
    public static int age(long birthday, long dateToCompare) {
        if (birthday > dateToCompare) {
            throw new IllegalArgumentException("Birthday is after dateToCompare!");
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateToCompare);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        final boolean isLastDayOfMonth = dayOfMonth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.setTimeInMillis(birthday);
        int age = year - cal.get(Calendar.YEAR);

        final int monthBirth = cal.get(Calendar.MONTH);
        if (month == monthBirth) {
            final int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            final boolean isLastDayOfMonthBirth = dayOfMonthBirth == cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            // 如果生日在当月，但是未达到生日当天的日期，年龄减一
            final boolean isSameMonthLessToday = (!isLastDayOfMonth || !isLastDayOfMonthBirth) && dayOfMonth < dayOfMonthBirth;
            if (isSameMonthLessToday) {
                age--;
            }
        } else if (month < monthBirth) {
            // 如果当前月份未达到生日的月份，年龄计算减一
            age--;
        }

        return age;
    }
}
