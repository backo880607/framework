package com.pisces.framework.core.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pisces.framework.core.utils.lang.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 多枚举
 *
 * @author jason
 * @date 2022/12/07
 */
public class MultiEnum<T extends Enum<T>> {
    private int value;

    public MultiEnum() {
    }

    protected MultiEnum(T value) {
        this.value = (1 << value.ordinal());
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void reset() {
        this.value = 0;
    }

    public boolean contains(T value) {
        return (this.value & (1 << value.ordinal())) > 0;
    }

    public boolean contains(MultiEnum<T> rhs) {
        return (this.value | rhs.value) == rhs.value;
    }

    public void add(T value) {
        this.value = this.value | (1 << value.ordinal());
    }

    public void add(MultiEnum<T> rhs) {
        this.value = this.value | rhs.value;
    }

    public void sub(T value) {
        this.value = this.value & ~(1 << value.ordinal());
    }

    public void sub(MultiEnum<T> rhs) {
        this.value = this.value & ~rhs.value;
    }

    public boolean equal(T value) {
        return this.value == (1 << value.ordinal());
    }

    public boolean equal(MultiEnum<T> rhs) {
        return this.value == rhs.value;
    }

    public List<T> getEnumList() {
        List<T> result = new ArrayList<>();
        Class<T> cls = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T[] enums = cls.getEnumConstants();
        for (int i = 0; i < enums.length; ++i) {
            int temp = 1 << i;
            if ((this.value & temp) != 0) {
                result.add(enums[i]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return StringUtils.join(getEnumList(), ";", Enum::name);
    }

    public void parse(String enumNames) {
        reset();
        Class<T> enumClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        String[] enums = enumNames.split(";");
        for (String enumName : enums) {
            if (enumName.isEmpty()) {
                continue;
            }
            add(Enum.valueOf(enumClazz, enumName));
        }
    }
}
