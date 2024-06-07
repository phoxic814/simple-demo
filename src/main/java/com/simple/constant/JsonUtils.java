package com.simple.constant;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static <T> T toObject(Object obj, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, clazz);
    }
}
