package com.pisces.framework.core.utils.lang;

import com.pisces.framework.core.validator.Matcher;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 字符串跑龙套
 *
 * @author jason
 * @date 2022/12/07
 */
public final class StringUtils {
    private static Pattern UNDERLINE_TO_CAMELHUMP_PATTERN = Pattern.compile("_[a-z]");

    private StringUtils() {
    }

    public static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(@Nullable Object str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static <T> String toString(T obj, Function<T, String> fun) {
        if (obj == null) {
            return "";
        }
        return fun != null ? fun.apply(obj) : obj.toString();
    }

    public static <T> String join(Collection<T> collection, String separator) {
        return join(collection.iterator(), separator, null);
    }

    public static <T> String join(Collection<T> collection, String separator, Function<T, String> fun) {
        if (collection == null) {
            return "";
        }

        return join(collection.iterator(), separator, fun);
    }

    public static <T> String join(Iterator<T> iterator, String separator, Function<T, String> fun) {
        if (iterator == null || !(iterator.hasNext())) {
            return "";
        }

        T first = iterator.next();
        if (!(iterator.hasNext())) {
            return StringUtils.toString(first, fun);
        }

        StringBuilder buf = new StringBuilder(256);
        buf.append(StringUtils.toString(first, fun));
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }

            buf.append(StringUtils.toString(iterator.next(), fun));
        }

        return buf.toString();
    }

    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param elements
     * @return 新拼接好的字符串
     * @see String#join(CharSequence, CharSequence...)
     */
    public static String join(String delimiter, CharSequence... elements) {
        if (CollectionUtils.isEmpty(elements)) {
            return "";
        } else if (elements.length == 1) {
            return String.valueOf(elements[0]);
        } else {
            return String.join(delimiter, elements);
        }
    }

    public static String getHead(String value, String sep) {
        final int index = value.indexOf(sep);
        return index >= 0 ? value.substring(0, index) : "";
    }

    public static String getTail(String value, String sep) {
        final int index = value.lastIndexOf(sep);
        return index >= 0 ? value.substring(index + sep.length()) : "";
    }

    /**
     * 第一个字符转换为小写
     *
     * @param string
     */
    public static String firstCharToLowerCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = string.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return string;
    }


    /**
     * 第一个字符转换为大写
     *
     * @param string
     */
    public static String firstCharToUpperCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = string.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return string;
    }

    /**
     * 对字符串进行!, *或则？匹配。
     */
    public static boolean matches(String str, String regex) {
        boolean negate = false;
        if (regex.startsWith("!")) {
            negate = true;
            regex = regex.substring(1);
        }
        // 如果str与regex完全相等，且str不包含反斜杠，则返回true。
        if (str.equals(regex) && str.indexOf('\\') < 0) {
            return !negate;
        }
        // 同时遍历源字符串与匹配表达式
        int rIdx = 0;
        int sIdx = 0;
        while (rIdx < regex.length() && sIdx < str.length()) {
            // 以匹配表达式为主导
            char c = regex.charAt(rIdx);
            switch (c) {
                // 匹配到*号进入下一层递归
                case '*':
                    // 去除前面已经完全匹配的前缀
                    String tempSource = str.substring(sIdx);
                    // 从星号后一位开始认为是新的匹配表达式
                    String tempRegex = regex.substring(rIdx + 1);
                    // 此处等号不能缺，如（ABCD，*），等号能达成("", *)条件
                    for (int j = 0; j <= tempSource.length(); j++) {
                        // 很普通的递归思路
                        if (matches(tempSource.substring(j), tempRegex)) {
                            return !negate;
                        }
                    }
                    // 排除所有潜在可能性，则返回false
                    return negate;
                case '?':
                    break;
                // 匹配到反斜杠跳过一位，匹配下一个字符串
                case '\\':
                    c = regex.charAt(++rIdx);
                default:
                    if (str.charAt(sIdx) != c) {
                        // 普通字符的匹配
                        return negate;
                    }
            }
            rIdx++;
            sIdx++;
        }
        // 最终str被匹配完全，而regex也被匹配完整或只剩一个*号
        boolean result = str.length() == sIdx &&
                (regex.length() == rIdx || regex.length() == rIdx + 1 && regex.charAt(rIdx) == '*');
        return negate != result;
    }

    /**
     * 替换指定字符串的指定区间内字符为"*"
     * 俗称：脱敏功能，后面其他功能，可以见：DesensitizedUtil(脱敏工具类)
     *
     * <pre>
     * StrUtil.hide(null,*,*)=null
     * StrUtil.hide("",0,*)=""
     * StrUtil.hide("jackduan@163.com",-1,4)   ****duan@163.com
     * StrUtil.hide("jackduan@163.com",2,3)    ja*kduan@163.com
     * StrUtil.hide("jackduan@163.com",3,2)    jackduan@163.com
     * StrUtil.hide("jackduan@163.com",16,16)  jackduan@163.com
     * StrUtil.hide("jackduan@163.com",16,17)  jackduan@163.com
     * </pre>
     *
     * @param str          字符串
     * @param startInclude 开始位置（包含）
     * @param endExclude   结束位置（不包含）
     * @return 替换后的字符串
     * @since 4.1.14
     */
    public static String hide(CharSequence str, int startInclude, int endExclude) {
        StringBuilder builder = new StringBuilder(str);
        return builder.replace(startInclude, endExclude, "*").toString();
    }

    /**
     * 是否为数字，支持包括：
     *
     * <pre>
     * 1、10进制
     * 2、16进制数字（0x开头）
     * 3、科学计数法形式（1234E3）
     * 4、类型标识形式（123D）
     * 5、正负数标识形式（+123、-234）
     * </pre>
     *
     * @param str 字符串值
     * @return 是否为数字
     */
    public static boolean isNumber(CharSequence str) {
        if (isBlank(str)) {
            return false;
        }
        char[] chars = str.toString().toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        int start = (chars[0] == '-' || chars[0] == '+') ? 1 : 0;
        if (sz > start + 1) {
            if (chars[start] == '0' && (chars[start + 1] == 'x' || chars[start + 1] == 'X')) {
                int i = start + 2;
                if (i == sz) {
                    // str == "0x"
                    return false;
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
        // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || (i < sz + 1 && allowSigns && !foundDigit)) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (false == foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                // we need a digit after the E
                foundDigit = false;
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent
                return foundDigit && !hasExp;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return false == allowSigns && foundDigit;
    }

    /**
     * 重复某个字符
     *
     * <pre>
     * StrUtil.repeat('e', 0)  = ""
     * StrUtil.repeat('e', 3)  = "eee"
     * StrUtil.repeat('e', -2) = ""
     * </pre>
     *
     * @param c     被重复的字符
     * @param count 重复的数目，如果小于等于0则返回""
     * @return 重复字符字符串
     */
    public static String repeat(char c, int count) {
        if (count <= 0) {
            return "";
        }

        char[] result = new char[count];
        for (int i = 0; i < count; i++) {
            result[i] = c;
        }
        return new String(result);
    }

    /**
     * 重复某个字符串到指定长度
     *
     * @param str    被重复的字符
     * @param padLen 指定长度
     * @return 重复字符字符串
     * @since 4.3.2
     */
    public static String repeatByLength(CharSequence str, int padLen) {
        if (null == str) {
            return null;
        }
        if (padLen <= 0) {
            return "";
        }
        final int strLen = str.length();
        if (strLen == padLen) {
            return str.toString();
        } else if (strLen > padLen) {
            return subPre(str, padLen);
        }

        // 重复，直到达到指定长度
        final char[] padding = new char[padLen];
        for (int i = 0; i < padLen; i++) {
            padding[i] = str.charAt(i % strLen);
        }
        return new String(padding);
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str              String
     * @param fromIndexInclude 开始的index（包括）
     * @param toIndexExclude   结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return "";
        }
        int len = str.length();

        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0) {
                fromIndexInclude = 0;
            }
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }

        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0) {
                toIndexExclude = len;
            }
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }

        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }

        if (fromIndexInclude == toIndexExclude) {
            return "";
        }

        return str.toString().substring(fromIndexInclude, toIndexExclude);
    }

    /**
     * 通过CodePoint截取字符串，可以截断Emoji
     *
     * @param str       String
     * @param fromIndex 开始的index（包括）
     * @param toIndex   结束的index（不包括）
     * @return 字串
     */
    public static String subByCodePoint(CharSequence str, int fromIndex, int toIndex) {
        if (isEmpty(str)) {
            return "";
        }

        if (fromIndex < 0 || fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }

        if (fromIndex == toIndex) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();
        final int subLen = toIndex - fromIndex;
        str.toString().codePoints().skip(fromIndex).limit(subLen).forEach(v -> sb.append(Character.toChars(v)));
        return sb.toString();
    }

    /**
     * 切割指定位置之前部分的字符串
     *
     * @param string         字符串
     * @param toIndexExclude 切割到的位置（不包括）
     * @return 切割后的剩余的前半部分字符串
     */
    public static String subPre(CharSequence string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    /**
     * 切割指定位置之后部分的字符串
     *
     * @param string    字符串
     * @param fromIndex 切割开始的位置（包括）
     * @return 切割后后剩余的后半部分字符串
     */
    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }

    /**
     * 切割指定长度的后部分的字符串
     *
     * <pre>
     * StrUtil.subSufByLength("abcde", 3)      =    "cde"
     * StrUtil.subSufByLength("abcde", 0)      =    ""
     * StrUtil.subSufByLength("abcde", -5)     =    ""
     * StrUtil.subSufByLength("abcde", -1)     =    ""
     * StrUtil.subSufByLength("abcde", 5)       =    "abcde"
     * StrUtil.subSufByLength("abcde", 10)     =    "abcde"
     * StrUtil.subSufByLength(null, 3)               =    null
     * </pre>
     *
     * @param string 字符串
     * @param length 切割长度
     * @return 切割后后剩余的后半部分字符串
     * @since 4.0.1
     */
    public static String subSufByLength(CharSequence string, int length) {
        if (isEmpty(string)) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        return sub(string, -length, string.length());
    }

    /**
     * 截取字符串,从指定位置开始,截取指定长度的字符串<br>
     * author weibaohui
     *
     * @param input     原始字符串
     * @param fromIndex 开始的index,包括
     * @param length    要截取的长度
     * @return 截取后的字符串
     */
    public static String subWithLength(String input, int fromIndex, int length) {
        return sub(input, fromIndex, fromIndex + length);
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串为空串""，则返回空串，如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * StrUtil.subBefore(null, *, false)      = null
     * StrUtil.subBefore("", *, false)        = ""
     * StrUtil.subBefore("abc", "a", false)   = ""
     * StrUtil.subBefore("abcba", "b", false) = "a"
     * StrUtil.subBefore("abc", "c", false)   = "ab"
     * StrUtil.subBefore("abc", "d", false)   = "abc"
     * StrUtil.subBefore("abc", "", false)    = ""
     * StrUtil.subBefore("abc", null, false)  = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return "";
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (-1 == pos) {
            return str;
        }
        if (0 == pos) {
            return "";
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之前的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""）或者分隔字符串为null，返回原字符串<br>
     * 如果分隔字符串未找到，返回原字符串，举例如下：
     *
     * <pre>
     * StrUtil.subBefore(null, *, false)      = null
     * StrUtil.subBefore("", *, false)        = ""
     * StrUtil.subBefore("abc", 'a', false)   = ""
     * StrUtil.subBefore("abcba", 'b', false) = "a"
     * StrUtil.subBefore("abc", 'c', false)   = "ab"
     * StrUtil.subBefore("abc", 'd', false)   = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subBefore(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : "";
        }

        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (-1 == pos) {
            return str;
        }
        if (0 == pos) {
            return "";
        }
        return str.substring(0, pos);
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * StrUtil.subAfter(null, *, false)      = null
     * StrUtil.subAfter("", *, false)        = ""
     * StrUtil.subAfter(*, null, false)      = ""
     * StrUtil.subAfter("abc", "a", false)   = "bc"
     * StrUtil.subAfter("abcba", "b", false) = "cba"
     * StrUtil.subAfter("abc", "c", false)   = ""
     * StrUtil.subAfter("abc", "d", false)   = ""
     * StrUtil.subAfter("abc", "", false)    = "abc"
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 3.1.1
     */
    public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : "";
        }
        if (separator == null) {
            return "";
        }
        final String str = string.toString();
        final String sep = separator.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (-1 == pos || (string.length() - 1) == pos) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 截取分隔字符串之后的字符串，不包括分隔字符串<br>
     * 如果给定的字符串为空串（null或""），返回原字符串<br>
     * 如果分隔字符串为空串（null或""），则返回空串，如果分隔字符串未找到，返回空串，举例如下：
     *
     * <pre>
     * StrUtil.subAfter(null, *, false)      = null
     * StrUtil.subAfter("", *, false)        = ""
     * StrUtil.subAfter("abc", 'a', false)   = "bc"
     * StrUtil.subAfter("abcba", 'b', false) = "cba"
     * StrUtil.subAfter("abc", 'c', false)   = ""
     * StrUtil.subAfter("abc", 'd', false)   = ""
     * </pre>
     *
     * @param string          被查找的字符串
     * @param separator       分隔字符串（不包括）
     * @param isLastSeparator 是否查找最后一个分隔字符串（多次出现分隔字符串时选取最后一个），true为选取最后一个
     * @return 切割后的字符串
     * @since 4.1.15
     */
    public static String subAfter(CharSequence string, char separator, boolean isLastSeparator) {
        if (isEmpty(string)) {
            return null == string ? null : "";
        }
        final String str = string.toString();
        final int pos = isLastSeparator ? str.lastIndexOf(separator) : str.indexOf(separator);
        if (-1 == pos) {
            return "";
        }
        return str.substring(pos + 1);
    }

    /**
     * 补充字符串以满足指定长度，如果提供的字符串大于指定长度，截断之
     * 同：leftPad (org.apache.commons.lang3.leftPad)
     *
     * <pre>
     * StrUtil.padPre(null, *, *);//null
     * StrUtil.padPre("1", 3, "ABC");//"AB1"
     * StrUtil.padPre("123", 2, "ABC");//"12"
     * StrUtil.padPre("1039", -1, "0");//"103"
     * </pre>
     *
     * @param str    字符串
     * @param length 长度
     * @param padStr 补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(CharSequence str, int length, CharSequence padStr) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == length) {
            return str.toString();
        } else if (strLen > length) {
            //如果提供的字符串大于指定长度，截断之
            return subPre(str, length);
        }

        return repeatByLength(padStr, length - strLen).concat(str.toString());
    }

    /**
     * 补充字符串以满足最小长度，如果提供的字符串大于指定长度，截断之
     * 同：leftPad (org.apache.commons.lang3.leftPad)
     *
     * <pre>
     * StrUtil.padPre(null, *, *);//null
     * StrUtil.padPre("1", 3, '0');//"001"
     * StrUtil.padPre("123", 2, '0');//"12"
     * </pre>
     *
     * @param str     字符串
     * @param length  长度
     * @param padChar 补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(CharSequence str, int length, char padChar) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == length) {
            return str.toString();
        } else if (strLen > length) {
            //如果提供的字符串大于指定长度，截断之
            return subPre(str, length);
        }

        return repeat(padChar, length - strLen).concat(str.toString());
    }

    /**
     * 补充字符串以满足最小长度，如果提供的字符串大于指定长度，截断之
     *
     * <pre>
     * StrUtil.padAfter(null, *, *);//null
     * StrUtil.padAfter("1", 3, '0');//"100"
     * StrUtil.padAfter("123", 2, '0');//"23"
     * StrUtil.padAfter("123", -1, '0')//"" 空串
     * </pre>
     *
     * @param str     字符串，如果为{@code null}，直接返回null
     * @param length  长度
     * @param padChar 补充的字符
     * @return 补充后的字符串
     */
    public static String padAfter(CharSequence str, int length, char padChar) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == length) {
            return str.toString();
        } else if (strLen > length) {
            //如果提供的字符串大于指定长度，截断之
            return sub(str, strLen - length, strLen);
        }

        return str.toString().concat(repeat(padChar, length - strLen));
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StrUtil.padAfter(null, *, *);//null
     * StrUtil.padAfter("1", 3, "ABC");//"1AB"
     * StrUtil.padAfter("123", 2, "ABC");//"23"
     * </pre>
     *
     * @param str    字符串，如果为{@code null}，直接返回null
     * @param length 长度
     * @param padStr 补充的字符
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String padAfter(CharSequence str, int length, CharSequence padStr) {
        if (null == str) {
            return null;
        }
        final int strLen = str.length();
        if (strLen == length) {
            return str.toString();
        } else if (strLen > length) {
            //如果提供的字符串大于指定长度，截断之
            return subSufByLength(str, length);
        }

        return str.toString().concat(repeatByLength(padStr, length - strLen));
    }

    /**
     * 比较两个字符串是否相等。
     *
     * @param str1       要比较的字符串1
     * @param str2       要比较的字符串2
     * @param ignoreCase 是否忽略大小写
     * @return 如果两个字符串相同，或者都是{@code null}，则返回{@code true}
     * @since 3.2.0
     */
    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同（忽略大小写），相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1 给定需要检查的字符串
     * @param strs 需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAnyIgnoreCase(CharSequence str1, CharSequence... strs) {
        return equalsAny(str1, true, strs);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1 给定需要检查的字符串
     * @param strs 需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, CharSequence... strs) {
        return equalsAny(str1, false, strs);
    }

    /**
     * 给定字符串是否与提供的中任一字符串相同，相同则返回{@code true}，没有相同的返回{@code false}<br>
     * 如果参与比对的字符串列表为空，返回{@code false}
     *
     * @param str1       给定需要检查的字符串
     * @param ignoreCase 是否忽略大小写
     * @param strs       需要参与比对的字符串列表
     * @return 是否相同
     * @since 4.3.2
     */
    public static boolean equalsAny(CharSequence str1, boolean ignoreCase, CharSequence... strs) {
        if (strs == null || strs.length == 0) {
            return false;
        }

        for (CharSequence str : strs) {
            if (equals(str1, str, ignoreCase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStr(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符列表中的任意一个字符
     *
     * @param str       指定字符串
     * @param testChars 需要检查的字符数组
     * @return 是否包含任意一个字符
     * @since 4.1.11
     */
    public static boolean containsAny(CharSequence str, char... testChars) {
        if (!isEmpty(str)) {
            int len = str.length();
            for (int i = 0; i < len; i++) {
                for (char testChar : testChars) {
                    if (testChar == str.charAt(i)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 给定字符串是否包含空白符（空白符包括空格、制表符、全角空格和不间断空格）<br>
     * 如果给定字符串为null或者""，则返回false
     *
     * @param str 字符串
     * @return 是否包含空白符
     * @since 4.0.8
     */
    public static boolean containsBlank(CharSequence str) {
        if (null == str) {
            return false;
        }
        final int length = str.length();
        if (0 == length) {
            return false;
        }

        for (int i = 0; i < length; i += 1) {
            if (CharUtils.isBlankChar(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || testStrs == null || testStrs.length == 0) {
            return null;
        }
        for (CharSequence checkStr : testStrs) {
            if (str.toString().contains(checkStr)) {
                return checkStr.toString();
            }
        }
        return null;
    }

    /**
     * 是否包含特定字符，忽略大小写，如果给定两个参数都为{@code null}，返回true
     *
     * @param str     被检测字符串
     * @param testStr 被测试是否包含的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
        if (null == str) {
            // 如果被监测字符串和
            return null == testStr;
        }
        return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     * @since 3.2.0
     */
    public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStrIgnoreCase(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串<br>
     * 忽略大小写
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     * @since 3.2.0
     */
    public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
        if (isEmpty(str) || testStrs == null || testStrs.length == 0) {
            return null;
        }
        for (CharSequence testStr : testStrs) {
            if (containsIgnoreCase(str, testStr)) {
                return testStr.toString();
            }
        }
        return null;
    }

    /**
     * 删除匹配的全部内容
     *
     * @param pattern 正则
     * @param content 被匹配的内容
     * @return 删除后剩余的内容
     */
    public static String delAll(Pattern pattern, CharSequence content) {
        if (null == pattern || isBlank(content)) {
            return content != null ? content.toString() : "";
        }

        return pattern.matcher(content).replaceAll("");
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     * @since 3.3.1
     */
    public static boolean contains(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return false;
        }
        return pattern.matcher(content).find();
    }

    /**
     * 去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
     */
    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str.toString();
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            // 截取后半段
            return subSuf(str2, prefix.length());
        }
        return str2;
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str.toString();
        }

        final String str2 = str.toString();
        if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
            // 截取后半段
            return subSuf(str2, prefix.length());
        }
        return str2;
    }

    /**
     * 字符串的每一个字符是否都与定义的匹配器匹配
     *
     * @param value   字符串
     * @param matcher 匹配器
     * @return 是否全部匹配
     * @since 3.2.3
     */
    public static boolean isAllCharMatch(CharSequence value, Matcher<Character> matcher) {
        if (StringUtils.isBlank(value)) {
            return false;
        }
        for (int i = value.length(); --i >= 0; ) {
            if (false == matcher.match(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean endsWithAny(String str, String... suffixes) {
        if (isBlank(str) || suffixes == null || suffixes.length == 0) {
            return false;
        }

        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
