package com.doctorq.appointmentservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeoutInMinutes) {
        if (value == null) return;

        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;

        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(timeoutInMinutes));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        if (key != null) {
            redisTemplate.delete(key);
        }
    }

    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }


}
