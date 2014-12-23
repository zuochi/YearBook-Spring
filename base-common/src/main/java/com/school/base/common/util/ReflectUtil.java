package com.school.base.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author work
 */
public class ReflectUtil {

    public static Field[] getFields(Class aClass) {
        Field[] fields = aClass.getDeclaredFields();
        while (aClass.getSuperclass() != null) {
            aClass = aClass.getSuperclass();
            fields = (Field[]) ArrayUtils.addAll(fields, aClass.getDeclaredFields());
        }
        if (fields != null) {
            for (Field field : fields) {
                field.setAccessible(true);
            }
        }
        return fields;
    }

    public static Method[] getMethods(Class aClass) {
        Method[] methods = aClass.getDeclaredMethods();
        while (aClass.getSuperclass() != null) {
            aClass = aClass.getSuperclass();
            methods = (Method[]) ArrayUtils.addAll(methods, aClass.getDeclaredMethods());
        }
        if (methods != null) {
            for (Method method : methods) {
                method.setAccessible(true);
            }
        }
        return methods;
    }
}
