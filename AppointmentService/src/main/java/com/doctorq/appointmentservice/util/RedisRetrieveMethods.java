package com.doctorq.appointmentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.doctorq.appointmentservice.util.Constants.timeToLive;

@Service
@RequiredArgsConstructor
public class RedisRetrieveMethods {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readCacheValue(Object cachedValue, TypeReference<T> typeRef) throws JsonProcessingException {
        if (cachedValue == null) return null;

        return objectMapper.readValue(cachedValue.toString(), typeRef);
    }

    public static <T> void setCacheValue(RedisUtil redisUtil, String key, T data) throws JsonProcessingException{
        redisUtil.set(key, objectMapper.writeValueAsString(data), timeToLive);
    }
}
