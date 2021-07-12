package com.im.imservice.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author zhuanning
 * @project mglive-docking
 * @create 2019-12-12 18:36
 */
public class FormJsonUtils {

    // 转换JSOn的对象
    private static ObjectMapper om = new ObjectMapper();

    static {
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    // 对象转JSON格式字符串
    public static String getObjectToJson(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // JSON格式的字符串转成对象
    public static <T> T getJsonToObject(String json, Class<T> beanType) {
        T t = null;
        try {
            t = om.readValue(json, beanType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    // 字符串转化为json对象
    public static String getStringToJson(String key, String value) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, value);
        return jsonObject.toString();
    }

}

