package com.metax.web.util;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @Author: hanabi
 * @DateTime: 2023/10/26 9:38
 **/
public class ConcurrentHashMapUtils {

    private static boolean IS_JAVA8;

    static {
        try {
            IS_JAVA8 = System.getProperty("java.version").startsWith("1.8.");
        } catch (Exception ignore) {
            // exception is ignored
            IS_JAVA8 = true;
        }
    }

    /**
     * Java 8 ConcurrentHashMap#computeIfAbsent 存在性能问题的临时解决方案
     * 详情：https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372
     */
    public static <K, V> V computeIfAbsent(ConcurrentMap<K, V> map, K key, Function<? super K, ? extends V> func) {
        if (IS_JAVA8) {
            V v = map.get(key);
            if (null == v) {
                v = map.computeIfAbsent(key, func);
            }
            return v;
        } else {
            return map.computeIfAbsent(key, func);
        }
    }
}
