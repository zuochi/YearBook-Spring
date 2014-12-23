package com.school.base.common.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 工具类的外观类，只负责将其它工具类暴露，本身不应有任何业务逻辑
 *
 * @author work
 */
public class Utils {

    public static <T> T random(Class<T> cls) {
        return ObjectUtil.random(cls);
    }

    public static <T> List<T> random(Class<T> cls, int number) {
        return ObjectUtil.random(cls, number);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return ObjectUtil.fromJson(json, classOfT);
    }

    public static String toJson(Object obj) {
        return ObjectUtil.toJson(obj);
    }
    public static Field[] getFields(Class aClass) {
        return ReflectUtil.getFields(aClass);
    }

    public static <T> void fill(T target, Map<String, Object> sourceMap) throws IOException {
        try {
            ObjectUtil.fill(target, sourceMap);
        } catch (IllegalArgumentException e) {
            throw new IOException(e);
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        }
    }

}
