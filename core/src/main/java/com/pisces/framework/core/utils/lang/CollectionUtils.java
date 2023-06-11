/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pisces.framework.core.utils.lang;

import java.util.*;
import java.util.function.Function;

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否为空
     *
     * @param array  数组对象
     * @return 空 true
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }


    /**
     * 判断数组是否不为空
     *
     * @param array 数组对象
     * @return 非空 true
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * 合并 list
     */
    public static <T> List<T> merge(List<T> list, List<T> other) {
        if (list == null && other == null) {
            return new ArrayList<>();
        } else if (isEmpty(other) && list != null) {
            return list;
        } else if (isEmpty(list)) {
            return other;
        }
        List<T> newList = new ArrayList<>(list);
        newList.addAll(other);
        return newList;
    }

    /**
     * 主要是用于修复 concurrentHashMap 在 jdk1.8 下的死循环问题
     *
     * @see <a href="https://bugs.openjdk.org/browse/JDK-8161372">https://bugs.openjdk.org/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> concurrentHashMap, K key, Function<? super K, ? extends V> mappingFunction) {
        V v = concurrentHashMap.get(key);
        if (v != null) {
            return v;
        }
        return concurrentHashMap.computeIfAbsent(key, mappingFunction);
    }

    /**
     * 合并两个数组为一个新的数组
     *
     * @param first  第一个数组
     * @param second 第二个数组
     * @return 新的数组
     */
    public static <T> T[] concat(T[] first, T[] second) {
        if (first == null && second == null) {
            throw new IllegalArgumentException("not allow first and second are null.");
        } else if (isEmpty(first) && second != null) {
            return second;
        } else if (isEmpty(second)) {
            return first;
        } else {
            T[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }


    /**
     * concat
     *
     * @param first  第一个
     * @param second 第二个
     * @param third  第三
     * @param others 其他人
     * @return {@link T[]}
     */
    public static <T> T[] concat(T[] first, T[] second, T[] third, T[]... others) {
        T[] results = concat(first, second);
        results = concat(results, third);

        if (others != null) {
            for (T[] other : others) {
                results = concat(results, other);
            }
        }
        return results;
    }


    /**
     * 可变长参形式数组
     *
     * @param first  第一个数组
     * @param second 第二个数组
     * @return 新的数组
     */
    @SafeVarargs
    public static <T> T[] append(T[] first, T... second) {
        if (first == null && second == null) {
            throw new IllegalArgumentException("not allow first and second are null.");
        } else if (isEmpty(first) && second != null) {
            return second;
        } else if (isEmpty(second)) {
            return first;
        } else {
            T[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }


    /**
     * 查看数组中是否包含某一个值
     *
     * @param arrays 数组
     * @param object 用于检测的值
     * @return true 包含
     */
    public static <T> boolean contains(T[] arrays, T object) {
        if (isEmpty(arrays)) {
            return false;
        }
        for (T array : arrays) {
            if (Objects.equals(array, object)) {
                return true;
            }
        }
        return false;
    }
}
