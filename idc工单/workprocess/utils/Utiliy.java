package com.workprocess.utils;

import org.apache.commons.lang.StringUtils;
import org.mockito.internal.verification.Times;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Utiliy {

    /**
     * 对象转换成Map
     *
     * @param obj 传入的对象
     * @return Map
     * @throws Exception 异常
     */
    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null)
            return null;

        Map<String, Object> map = new HashMap<>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter != null ? getter.invoke(obj) : null;
            map.put(key, value);
        }

        return map;
    }

    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method setter = property.getWriteMethod();
            if (map.keySet().contains(key)) {
                if (setter != null) {
                    if (!map.get(key).equals("null")) {
                        if (property.getPropertyType() == Timestamp.class) {
                            setter.invoke(obj, Timestamp.valueOf(String.valueOf(map.get(key))));
                        } else if (property.getPropertyType() == Integer.class) {
                            setter.invoke(obj, Integer.valueOf(String.valueOf(map.get(key))));
                        } else if (property.getPropertyType() == Double.class) {
                            setter.invoke(obj, Double.valueOf(String.valueOf(map.get(key))));
                        } else if (property.getPropertyType() == Boolean.class) {
                            setter.invoke(obj, Boolean.valueOf(String.valueOf(map.get(key))));
                        } else {
                            setter.invoke(obj, map.get(key));
                        }
                    }
                }
            }
        }
        return obj;
    }

    public static String setString(String str) {
        String[] userArray = str.split(",");
        Set<String> userSet = new HashSet<>();
        for (int i = 0; i < userArray.length; i++) {
            userSet.add(String.valueOf(userArray[i]));
        }
        str = StringUtils.join(userSet.toArray(), ",");
        return str;
    }

    /**
     * WorkProcess附件存放根目录
     *
     * @return String
     */
    public static String workProcessFilePath() {
        return System.getProperty("catalina.home");
    }

    public static String bubbleSort(String data) {
        int temp;
        String[] times = data.split(",");
        int size = times.length;
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (Integer.valueOf(times[j]) > Integer.valueOf(times[j + 1])) {
                    temp = Integer.valueOf(times[j]);
                    times[j] = times[j + 1];
                    times[j + 1] = String.valueOf(temp);
                }
            }
        }
        data = "";
        for (int i = 0; i < times.length; i++) {
            data += "," + times[i];
        }
        return data.substring(1);
    }

    public static void main(String[] args) {
        String r = "5,2,3,4,1,7,6";
        System.out.println(bubbleSort(r));
    }
}
