package com.xk.ksonlib;


import com.xk.ksonlib.json.JSONArray;
import com.xk.ksonlib.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuekai on 2017/11/12.
 */

public class KSon {

    private static final int TYPE_OBJECT = 0;
    private static final int TYPE_ARRAY = 1;

    /**
     * 转换成数组
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> toListModel(String json, Class<T> clazz) throws Exception {
        List<T> objects;
        if (json.charAt(0) == '[') {
            objects = new ArrayList<>();
            toArrayList(json, objects, clazz);
        } else {
            throw new Exception("错误的json串");
        }
        return objects;
    }

    /**
     * 转成对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T toModel(String json, Class<T> clazz) throws Exception {
        T object;

        if (json.charAt(0) == '{') {
            object = clazz.newInstance();
            toObject(json, object);
        } else {
            throw new Exception("错误的json串");
        }
        return object;
    }

    private static void toArrayList(String json, List list, Class clazz) throws Exception {
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            switch (getJsonType(jsonObject.toString())) {
                case TYPE_OBJECT:
                    Object o = clazz.newInstance();
                    toObject(jsonObject.toString(), o);
                    list.add(o);
                    break;
                case TYPE_ARRAY:
                    break;
                default:
                    System.out.println("错误");
                    break;
            }
        }
    }

    private static void toArray(String json, Object arrays, Class clazz) throws Exception {
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);

            switch (getJsonType(jsonObject.toString())) {
                case TYPE_OBJECT:
                    Object o = clazz.newInstance();
                    toObject(jsonObject.toString(), o);
                    Array.set(arrays, i, o);
//                    list.add(o);
                    break;
                case TYPE_ARRAY:
                    break;
                default:
                    System.out.println("错误");
                    break;
            }
        }
    }


    private static int getJsonType(String json) {
        if (json.charAt(0) == '{') {
            return TYPE_OBJECT;
        } else if (json.charAt(0) == '[') {
            return TYPE_ARRAY;
        } else {
            return -1;
        }
    }

    private static void toObject(String json, Object object) throws Exception {
        JSONObject jsonObject = new JSONObject(json);
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String keyName = keys.next();
            Object keyValue = jsonObject.get(keyName);
            if (keyValue instanceof JSONObject) {
                List<Field> fields = new ArrayList<>();
                getAllFields(object.getClass(), fields);
                for (Field field : fields) {
                    if (field.getName().equals(keyName)) {
                        Class clazz = (Class) field.getGenericType();//获取到当前JsonObject的class，从而构造这个jsonobject
                        Object o = clazz.newInstance();
                        toObject(keyValue.toString(), o);
                        field.setAccessible(true);
                        field.set(object, o);
                    }
                }
            } else if (keyValue instanceof JSONArray) {
                ArrayList arrayList = null;//集合
                Object array = null;//数组

                JSONArray jsonArray = new JSONArray(keyValue.toString());


                List<Field> fields1 = new ArrayList<>();//这个fields是集合中元素的
                getAllFields(object.getClass(), fields1);
                for (Field field : fields1) {
                    if (field.getName().equals(keyName)) {
                        Type type = field.getGenericType();
                        if (type instanceof ParameterizedType) {
                            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                            Class clazz = (Class) actualTypeArguments[0];//集合的元素类型
                            arrayList = new ArrayList();
                            toArrayList(jsonArray.toString(), arrayList, clazz);
                        } else {
                            Class componentType = ((Class) field.getGenericType()).getComponentType();
                            array = Array.newInstance(componentType, jsonArray.length());
                            toArray(jsonArray.toString(), array, componentType);
                        }
                    }
                }


                List<Field> fields = new ArrayList<>();//这个field是集合的父亲的
                getAllFields(object.getClass(), fields);
                for (Field field : fields) {
                    if (field.getName().equals(keyName)) {
                        field.setAccessible(true);
                        try {
                            if (arrayList != null) {
                                field.set(object, arrayList);
                            }
                            if (array != null) {
                                field.set(object, array);
                            }
                        } catch (Exception e) {
                            //字符串，如果为null，会catch
//                            e.printStackTrace();
                        }
                    }
                }

            } else {
                List<Field> fields = new ArrayList<>();
                getAllFields(object.getClass(), fields);
                for (Field field : fields) {
                    if (field.getName().equals(keyName)) {
                        field.setAccessible(true);
                        try {
                            field.set(object, keyValue);
                        } catch (Exception e) {
//                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public static String toJson(Object bean) throws Exception {
        StringBuffer result = new StringBuffer();
        if (bean == null) {
            return result.toString();
        }
        if (bean instanceof List<?>) {//集合
            addListToJson((List<?>) bean, result);
        } else if (bean instanceof Object[]) {//数组
            addArrayToJson((Object[]) bean, result);
        } else {//对象
            addObjectToJson(bean, result);
        }

        return result.toString();
    }

    /**
     * 为json字符串添加一个对象
     * 对象的非final以及所有除object以外的父类的属性都要遍历到
     *
     * @param bean
     * @param result
     */
    private static void addObjectToJson(Object bean, StringBuffer result) throws IllegalAccessException {
        if (bean == null) {
            result.append("null");
            return;
        }
        if (bean instanceof Integer ||
                bean instanceof Float ||
                bean instanceof Boolean ||
                bean instanceof Byte ||
                bean instanceof Long ||
                bean instanceof Short ||
                bean instanceof Character ||
                bean instanceof Double) {
            result.append(bean);
            return;
        } else if (bean instanceof String) {
            result.append("\"" + bean + "\"");
            return;
        }

        result.append("{");

        List<Field> fields = new ArrayList<>();
        getAllFields(bean.getClass(), fields);

        for (Field field : fields) {
            field.setAccessible(true);
            result.append("\"" + field.getName() + "\"" + ":");
            Object value = field.get(bean);
            //添加value（基本数据类型、引用类型、字符串、list、map）
            if (value == null) {
                result.append("null");
            } else if (value instanceof String) {
                result.append("\"" + value + "\"");
            } else if (value instanceof List<?>) {
                addListToJson((List<?>) value, result);
            } else if (value instanceof Object[]) {
                addArrayToJson((Object[]) value, result);
            } else if (value instanceof Map<?, ?>) {
                addMapToJson((Map<?, ?>) value, result);

            } else if (value instanceof Integer ||
                    value instanceof Float ||
                    value instanceof Boolean ||
                    value instanceof Byte ||
                    value instanceof Long ||
                    value instanceof Short ||
                    value instanceof Character ||
                    value instanceof Double) {
                result.append(value);
            } else {
                addObjectToJson(value, result);
            }
            result.append(",");
        }
        //如果最后一位是逗号，就删除
        deleteLastComma(result);
        result.append("}");
    }

    private static void deleteLastComma(StringBuffer result) {
        if (result.charAt(result.length() - 1) == ',') {
            result.deleteCharAt(result.length() - 1);
        }
    }

    /**
     * 获取所有的field，除了final修饰的
     *
     * @param selfClazz
     * @return
     */
    private static void getAllFields(Class selfClazz, List<Field> fields) {
        if (selfClazz == null) {
            return;
        }
        //把自己的非final参数存起来
        Field[] declaredFields = selfClazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isFinal(declaredField.getModifiers())) {
                fields.add(declaredField);
            }
        }
        getAllFields(selfClazz.getSuperclass(), fields);
    }

    /**
     * 为json字符串添加一个数组
     *
     * @param beans
     * @param result
     */
    private static void addArrayToJson(Object[] beans, StringBuffer result) throws IllegalAccessException {
        addListToJson(Arrays.asList(beans), result);
    }

    /**
     * 为json字符串添加一个list集合
     *
     * @param beans
     * @param result
     */
    private static void addListToJson(List<?> beans, StringBuffer result) throws IllegalAccessException {
        result.append("[");
        for (Object bean : beans) {
            addObjectToJson(bean, result);
            result.append(",");
        }
        deleteLastComma(result);
        result.append("]");
    }

    /**
     * 为json字符串添加一个map集合
     *
     * @param beans
     * @param result
     */
    private static void addMapToJson(Map<?, ?> beans, StringBuffer result) throws IllegalAccessException {
        result.append("{");

        Set<? extends Map.Entry<?, ?>> entries = beans.entrySet();
        for (Map.Entry<?, ?> entry : entries) {
            addObjectToJson(entry.getKey(), result);
            result.append(":");
            addObjectToJson(entry.getValue(), result);
            result.append(",");

        }
        deleteLastComma(result);
        result.append("}");
    }
}
