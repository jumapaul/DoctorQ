package com.doctorq.doctorservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_REGISTRY_PREFIX = "key_registry::";

    public void set(String key, Object value, long timeoutInMinutes) {
        if (value == null) return;

        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(timeoutInMinutes));
    }

    public void setGroup(String key, Object value) {
        if (value == null) return;

        if (value instanceof Collection && ((Collection<?>) value).isEmpty()) return;

        redisTemplate.opsForValue().set(key, value);

        //Register key group
        String registerKey = KEY_REGISTRY_PREFIX + resolveGroup(key);

        redisTemplate.opsForSet().add(registerKey, key);
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis fetching error");
            return null;
        }
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("--------------->redis delete error: {}", e.getMessage());
        }
    }

    public void deleteGroup(String groupName) {
        try {
            String registerKey = KEY_REGISTRY_PREFIX + groupName;

            Set<Object> keys = redisTemplate.opsForSet().members(registerKey);

            if (keys == null || keys.isEmpty()) return;

            List<String> keysToDelete = keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            keysToDelete.add(registerKey);

            redisTemplate.delete(keysToDelete);
        } catch (Exception e) {
            log.error("--------->Error deleting group: {}", e.getMessage());
        }
    }

    private String resolveGroup(String key) {
        return key.contains("::") ? key.split("::")[0] : key;
    }
}
 