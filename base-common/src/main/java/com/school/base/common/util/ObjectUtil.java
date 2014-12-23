package com.school.base.common.util;

import static com.school.base.common.util.ObjectUtil.isEmpty;
import static com.school.base.common.util.ObjectUtil.random;
import static com.school.base.common.util.ObjectUtil.randomIndex;
import static com.school.base.common.util.ObjectUtil.randomObject;
import static com.school.base.common.util.Utils.getFields;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * 对象工具类
 *
 * @author work
 */
public class ObjectUtil {

    @SuppressWarnings("rawtypes")
    public static <T> T random(Class<T> cls) {
        try {
            T obj = cls.newInstance();
            BeanInfo info = Introspector.getBeanInfo(cls);
            MethodDescriptor[] descriptors = info.getMethodDescriptors();
            for (MethodDescriptor descriptor : descriptors) {
                if (descriptor.getName().startsWith("set")) {
                    Class[] parameterClssses = descriptor.getMethod().getParameterTypes();
                    if (parameterClssses != null && parameterClssses.length == 1) {
                        Class parameterCls = parameterClssses[0];
                        Object parameter = randomParameter(parameterCls);
                        descriptor.getMethod().invoke(obj, parameter);
                    }
                }
            }

            return obj;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> random(Class<T> cls, int number) {
        try {
            List<T> list = new ArrayList<T>();
            int count = 0;
            while (count++ < number) {
                list.add(random(cls));
            }
            return list;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String randomString(boolean letters, boolean digits, boolean chinese) {
        return randomString(letters, digits, chinese, -1);
    }

    public static String randomString(boolean letters, boolean digits, boolean chinese, int fixLen) {
        String value = (letters ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ" : "")
                + (digits ? "123456789" : "")
                + (chinese ? "欢迎你我是中文随机测试" : "");
        int len = (int) (Math.random() * 10);
        len = Math.max(6, len);
        if (fixLen > 0) {
            len = fixLen;
        }

        StringBuilder builder = new StringBuilder();
        int count = 0;
        while (count++ < len) {
            int index = (int) (Math.random() * value.length());
            index = Math.min(value.length() - 1, index);
            index = Math.max(0, index);
            builder.append(value.charAt(index));
        }
        return builder.toString();
    }

    @SuppressWarnings("rawtypes")
    public static Object randomParameter(Class parameterCls) throws IOException {
        if (parameterCls == String.class || parameterCls == Clob.class) {
            String value = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789欢迎你我是中文随机测试";
            int len = (int) (Math.random() * 10);
            len = Math.max(1, len);

            StringBuilder builder = new StringBuilder();
            int count = 0;
            while (count++ < len) {
                int index = (int) (Math.random() * value.length());
                index = Math.min(value.length() - 1, index);
                index = Math.max(0, index);
                builder.append(value.charAt(index));
            }
            return builder.toString();
        } else if (parameterCls == Integer.class || parameterCls == int.class) {
            return (int) (Math.random() * 1000);
        } else if (parameterCls == Double.class || parameterCls == double.class) {
            double value = (double) (Math.random() * 1000);
            return value;
        } else if (parameterCls == Date.class) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            Calendar calendar = Calendar.getInstance();
            calendar.set(randomObject(year), randomIndex(12), randomIndex(28) + 1,
                    randomIndex(60) + 1, randomIndex(60) + 1, randomIndex(60) + 1);
            Date date = calendar.getTime();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.add(Calendar.DAY_OF_MONTH, -1);
            if (date.after(calendar1.getTime())) {
                return randomParameter(Date.class);
            } else {
                return date;
            }

        } else if (parameterCls == Boolean.class || parameterCls == boolean.class) {
            return Math.random() > 0.5 ? true : false;
        } else if (parameterCls == byte[].class) {
            return new byte[0];
        } else if (parameterCls == Map.class || parameterCls == HashMap.class) {
            return new HashMap();
        } else if (parameterCls.isArray()) {
            return new Object[0];
        } else if (parameterCls == List.class || parameterCls == ArrayList.class) {
            return new ArrayList();
        }else if (parameterCls.isEnum()) {
            try {
                Object[] objs = (Object[]) parameterCls.getMethod("values").invoke(parameterCls);
                int index = (int) (Math.random() * objs.length);
                index = Math.min(objs.length - 1, index);
                index = Math.max(0, index);
                return objs.length > 0 ? objs[index] : null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object convert(String value, Class cls) {
        if (cls == String.class) {
            return isEmpty(value) ? null : value;
        } else if (cls == Boolean.class) {
            return isEmpty(value) ? null : Boolean.valueOf(value);
        } else if (cls == boolean.class) {
            return isEmpty(value) ? false : Boolean.valueOf(value).booleanValue();
        } else if (cls == Integer.class) {
            return isEmpty(value) ? null : Integer.valueOf(value);
        } else if (cls == int.class) {
            return isEmpty(value) ? 0 : Integer.valueOf(value).intValue();
        } else if (cls == Long.class) {
            return isEmpty(value) ? null : Long.valueOf(value);
        } else if (cls == long.class) {
            return isEmpty(value) ? 0L : Long.valueOf(value).longValue();
        } else if (cls == Double.class) {
            return isEmpty(value) ? null : Double.valueOf(value);
        } else if (cls == double.class) {
            return isEmpty(value) ? 0D : Double.valueOf(value).doubleValue();
        } else if (cls == Float.class) {
            return isEmpty(value) ? null : Float.valueOf(value);
        } else if (cls == float.class) {
            return isEmpty(value) ? 0F : Float.valueOf(value).floatValue();
        } else if (cls == Date.class) {
            return isEmpty(value) ? null : new Date(Long.valueOf(value));
        } else if (cls == Integer[].class) {
            Integer[] values = new Integer[1];
            if (isEmpty(value)) {
                values = new Integer[0];
            } else {
                values[0] = Integer.valueOf(value);
            }
            return values;
        } else {
            throw new UnsupportedOperationException("暂时未支持的转换类型: " + cls.getName());
        }
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String randomName() {
        String name1 = "张李王赵周黄廖陈杨钱孙罗唐朱吴孟容刘何";
        String name2 = "一二三四五六七八九十";
        int index1 = randomIndex(name1.length());
        int index2 = randomIndex(name2.length());
        return name1.substring(index1, index1 + 1) + name2.substring(index2, index2 + 1);
    }

    public static int randomIndex(int len) {
        int index = (int) (Math.random() * len);
        index = Math.max(index, 0);
        index = Math.min(index, len - 1);
        return index;
    }

    public static <T> T randomObject(T... objs) {
        int index = randomIndex(objs.length);
        return objs[index];
    }

    public static String numberToStr(int num, int len) {
        String str = num + "";
        while (str.length() < len) {
            str = "0" + str;
        }
        return str;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            json = URLDecoder.decode(json, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json.substring(0, json.length() - 1), classOfT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void fill(T target, Map<String, Object> sourceMap) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = getFields(target.getClass());
        for (Field field : fields) {
            String name = field.getName();
            if (sourceMap.containsKey(name) && sourceMap.get(name) != null) {
                field.set(target, ObjectUtil.convert(sourceMap.get(name).toString(), field.getType()));
            }
        }
    }
}
