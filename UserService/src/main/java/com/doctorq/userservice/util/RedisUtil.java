package com.doctorq.userservice.util;

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

        //Register key into group
        String registryKey = KEY_REGISTRY_PREFIX + resolveGroup(key);
        redisTemplate.opsForSet().add(registryKey, key);

        log.info("----->Redis cached and registry key: {} into {}", key, registryKey);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("--------------->redis delete error: {}", e.getMessage());
        }
    }

    //For list eviction
    public void deleteGroup(String groupName) {
        try {
            String registryKey = KEY_REGISTRY_PREFIX + groupName;
            Set<Object> keys = redisTemplate.opsForSet().members(registryKey);

            if (keys == null || keys.isEmpty()) return;

            List<String> keysToDelete = keys.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            keysToDelete.add(registryKey);

            redisTemplate.delete(keysToDelete);

        } catch (Exception e) {
            log.error("---------->Error deleting group: {}", e.getMessage());
        }
    }


    private String resolveGroup(String key) {
        return key.contains("::") ? key.split("::")[0] : key;
    }
}
