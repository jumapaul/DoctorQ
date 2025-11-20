package com.doctorq.favoriteservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;


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

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return clazz.cast(value);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
