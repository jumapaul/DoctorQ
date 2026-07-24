package com.doctorq.userservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import static com.doctorq.userservice.util.Constants.timeToLive;

@Service
public class RedisReadWriteMethods {
    private RedisReadWriteMethods() {
        /* This utility class should not be instantiated */
    }

    //Mapping json objects to string and vice versa

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readCacheValue(Object cachedData, TypeReference<T> typeRef) throws JsonProcessingException {
        if (cachedData == null) return null;
        return objectMapper.readValue(cachedData.toString(), typeRef);
    }

    public static <T> void setCacheValue(RedisUtil redisUtil, String key, T value) throws JsonProcessingException {
        redisUtil.set(key, objectMapper.writeValueAsString(value), timeToLive);
    }

    public static <T> void setGroupCacheValue(RedisUtil redisUtil, String key, T value) throws JsonProcessingException {
        redisUtil.setGroup(key, objectMapper.writeValueAsString(value));
    }
}
