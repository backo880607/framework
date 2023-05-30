package com.pisces.framework.core.entity;

import com.pisces.framework.core.utils.lang.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;

public class Duration {
    /**
     * 日志
     */
    private static final Logger log = LoggerFactory.getLogger(Duration.class);

    /**
     * ev字符串类型
     *
     * @author jason
     * @date 2022/12/07
     */
    enum EV_STRING_TYPE {
        /**
         * 电动汽车好
         */
        EVS_GOOD,
        /**
         * 电动汽车有p
         */
        EVS_HAVE_P,
    }

    static class EV_STRING implements Cloneable {
        /**
         * 类型
         */
        EV_STRING_TYPE type;
        /**
         * 价值
         */
        String value = "";

        public EV_STRING clone() {
            EV_STRING o = null;
            try {
                o = (EV_STRING) super.clone();
            } catch (CloneNotSupportedException e) {
                log.error("error", e);
            }

            return o;
        }
    }

    /**
     * 精度
     */
    public static final double PRECISION = 0.00001;
    /**
     * 无效
     */
    public static final Duration INVALID = new Duration("");
    /**
     * 价值
     */
    private String value;
    /**
     * 时间
     */
    private int time;
    /**
     * 率
     */
    private double rate;
    /**
     * b有效
     */
    private boolean bValid;

    /**
     * 持续时间
     *
     * @param value 价值
     */
    public Duration(String value) {
        setValue(value);
    }

    /**
     * 持续时间
     *
     * @param time 时间
     */
    public Duration(int time) {
        setTime(time);
    }

    /**
     * 设置值
     *
     * @param value 价值
     */
    public void setValue(String value) {
        this.value = value == null ? "" : value;
        this.time = 0;
        this.rate = 0.0f;
        parse();
    }

    public String getValue() {
        return this.value;
    }

    /**
     * 有效
     *
     * @return boolean
     */
    public boolean valid() {
        return this.bValid;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getTimeInMillis() {
        return this.time * DateUtils.PER_SECOND;
    }

    @Override
    public String toString() {
        if (this.rate > Duration.PRECISION) {
            return this.getValue();
        }

        if (this.time < 0) {
            return "";
        }
        if (this.time == 0) {
            return "0S";
        }

        int iMaxTimeUnit = 4;
        StringBuilder strExtraString = new StringBuilder();
        int extraTime = this.time;
        if (iMaxTimeUnit >= 4) {
            int i = extraTime / 86400;
            if (i > 0) {
                strExtraString.append(i).append("D");
            }
            extraTime -= i * 86400;
        }
        if (iMaxTimeUnit >= 3) {
            int i = extraTime / 3600;
            if (i > 0) {
                strExtraString.append(i).append("H");
            }
            extraTime -= i * 3600;
        }
        if (iMaxTimeUnit >= 2) {
            int i = extraTime / 60;
            if (i > 0) {
                strExtraString.append(i).append("M");
            }
            extraTime -= i * 60;
        }
        if (iMaxTimeUnit >= 1) {
            if (extraTime > 0) {
                strExtraString.append(extraTime).append("S");
            }
        }
        return strExtraString.toString();
    }

    /**
     * 有时间
     *
     * @param amount 量
     * @return long
     */
    public long getTime(double amount) {
        if (amount < PRECISION) {
            return getTime();
        }

        double result = (amount * this.rate) + this.time;
        return (long) Math.ceil(result);
    }

    /**
     * 有时间在飞船
     *
     * @param amount 量
     * @return long
     */
    public long getTimeInMillis(double amount) {
        return this.getTime(amount) * DateUtils.PER_SECOND;
    }

    private void parse() {
        this.bValid = true;
        if (this.value.isEmpty() || this.value.equals("-1")) {
            this.time = 0;
            this.rate = 0.0f;
            this.bValid = false;
            return;
        }

        if (!valueValid()) {
            this.time = 0;
            this.rate = 0.0f;
            this.bValid = false;
            return;
        }

        ArrayList<EV_STRING> lst = new ArrayList<>();
        decompound(lst);

        boolean oFlg = lst.size() == 1;

        for (EV_STRING evs : lst) {
            if (evs.type == EV_STRING_TYPE.EVS_GOOD) {
                AbstractMap.SimpleEntry<Boolean, Integer> extraTime = transTimeEach(evs.value);
                this.time += (extraTime.getKey() || !oFlg) ? extraTime.getValue() : extraTime.getValue();
            } else {
                AbstractMap.SimpleEntry<Boolean, Double> extraPer = transRateEach(evs.value);
                if (extraPer.getKey()) {
                    this.rate = extraPer.getValue();
                }
            }
        }

        if (this.time < 0) {
            this.time = 0;
            this.rate = 0.0f;
            this.bValid = false;
        }
    }

    private boolean valueValid() {
        for (char ch : this.value.toCharArray()) {
            if (ch <= 'z' && ch >= 'a') {
                ch -= 32;
            }

            if (!isNumber(ch) && !isUnits(ch)) {
                return false;
            }
        }

        this.value = this.value.toUpperCase();
        return true;
    }

    private boolean isNumber(char ch) {
        return ch <= '9' && ch >= '0' || ch == '.';
    }

    private boolean isUnits(char ch) {
        return ch == 'D' || ch == 'H'
                || ch == 'M' || ch == 'S' || ch == '+' || ch == '-' || ch == 'P';
    }

    private void decompound(ArrayList<EV_STRING> lst) {
        int len = this.value.length();
        int iStr = 0;
        EV_STRING evs = new EV_STRING();
        evs.type = EV_STRING_TYPE.EVS_GOOD;
        while (iStr < len) {
            char ch = this.value.charAt(iStr);
            if (ch == '+') {
                lst.add(evs.clone());
                evs.value = "";
                evs.type = EV_STRING_TYPE.EVS_GOOD;
                iStr++;
                continue;
            } else if (ch == '-') {
                int iPre = iStr;
                if (iPre != 0) {
                    iPre--;
                    char cp = this.value.charAt(iPre);
                    if (!(cp == 'D' || cp == 'H'
                            || cp == 'M' || cp == 'S')) {
                        evs.value = evs.value + 'S';
                    }
                }
            } else if (ch == 'P') {
                int iTempIndex = 0;
                StringBuilder temp1 = new StringBuilder();
                StringBuilder temp2 = new StringBuilder();
                int iTemp = evs.value.length() - 1;
                for (; iTemp >= 0; iTemp--) {
                    char cTemp = evs.value.charAt(iTemp);
                    if (cTemp == 'D' || cTemp == 'H'
                            || cTemp == 'M' || cTemp == 'S') {
                        iTempIndex++;
                    }
                    if (iTempIndex >= 2) {
                        temp1.insert(0, cTemp);
                    } else {
                        temp2.insert(0, cTemp);
                    }
                }
                if (!temp1.toString().isEmpty()) {
                    EV_STRING evs1 = new EV_STRING();
                    evs1.type = EV_STRING_TYPE.EVS_GOOD;
                    evs1.value = temp1.toString();
                    lst.add(evs1);
                }
                evs.value = temp2.toString();
                evs.type = EV_STRING_TYPE.EVS_HAVE_P;
                evs.value = evs.value + ch;
                iStr++;
                if (iStr != len) {
                    char cp = this.value.charAt(iStr);
                    if (cp == 'D' || cp == 'H'
                            || cp == 'M' || cp == 'S') {
                        evs.value = evs.value + cp;
                        iStr++;
                    }
                }

                lst.add(evs.clone());
                evs.value = "";
                evs.type = EV_STRING_TYPE.EVS_GOOD;
                continue;
            }
            evs.value = evs.value + ch;
            iStr++;
        }
        if (!evs.value.isEmpty()) {
            lst.add(evs);
        }
    }

    private AbstractMap.SimpleEntry<Boolean, Integer> transTimeEach(String extraValue) {
        if (extraValue.isEmpty()) {
            return new AbstractMap.SimpleEntry<>(false, 0);
        }

        int extraTime = 0;
        StringBuilder temp = new StringBuilder();
        boolean flg = false;
        for (int i = 0; i < extraValue.length(); i++) {
            char ch = extraValue.charAt(i);
            final int rate = timeUnit(ch);
            if (rate > 0) {
                double iC = Double.parseDouble(temp.toString());
                extraTime += (int) (iC * rate);
                temp.setLength(0);
                flg = true;
            } else {
                temp.append(ch);
            }
        }
        if (!temp.toString().isEmpty()) {
            extraTime += Double.parseDouble(temp.toString());
        }
        return new AbstractMap.SimpleEntry<>(flg, extraTime);
    }

    private AbstractMap.SimpleEntry<Boolean, Double> transRateEach(String extraValue) {
        if (extraValue.isEmpty()) {
            return new AbstractMap.SimpleEntry<>(false, 0.0);
        }

        double extraPer = 0.0;
        StringBuilder temp = new StringBuilder();
        boolean flg = false;
        for (int i = 0; i < extraValue.length(); i++) {
            char ch = extraValue.charAt(i);
            switch (ch) {
                case 'D':
                    extraPer = flg ? (1 / Double.parseDouble(temp.toString())) * 86400 : Double.parseDouble(temp.toString()) * 86400;
                    return new AbstractMap.SimpleEntry<>(true, extraPer);
                case 'H':
                    extraPer = flg ? (1 / Double.parseDouble(temp.toString())) * 3600 : Double.parseDouble(temp.toString()) * 3600;
                    return new AbstractMap.SimpleEntry<>(true, extraPer);
                case 'M':
                    extraPer = flg ? (1 / Double.parseDouble(temp.toString())) * 60 : Double.parseDouble(temp.toString()) * 60;
                    return new AbstractMap.SimpleEntry<>(true, extraPer);
                case 'S':
                    extraPer = flg ? (1 / Double.parseDouble(temp.toString())) * 1 : Double.parseDouble(temp.toString()) * 1;
                    return new AbstractMap.SimpleEntry<>(true, extraPer);
                case 'P':
                    flg = true;
                    break;
                default:
                    temp.append(ch);
            }
        }
        return new AbstractMap.SimpleEntry<>(true, extraPer);
    }

    private int timeUnit(char ch) {
        switch (ch) {
            case 'D':
                return 86400;
            case 'H':
                return 3600;
            case 'M':
                return 60;
            case 'S':
                return 1;
            default:
                break;
        }
        return 0;
    }
}
