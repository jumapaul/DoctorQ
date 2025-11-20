package com.doctorq.doctorservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.doctorq.doctorservice.utils.Constants.timeToLive;

@Service
@RequiredArgsConstructor
public class RedisRetrieveMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readCacheValue(Object cachedData, TypeReference<T> typeRef) throws JsonProcessingException {
        if (cachedData == null) return null;
        return objectMapper.readValue(cachedData.toString(), typeRef);
    }

    public static <T> void setCacheValue(RedisUtil redisUtil, String key, T value) throws JsonProcessingException {
        redisUtil.set(key, objectMapper.writeValueAsString(value), timeToLive);
    }
}
