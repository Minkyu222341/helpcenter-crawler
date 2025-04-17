package com.helpcentercrawl.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> T getObject(String key, Class<T> classType) {
        String jsonData = redisTemplate.opsForValue().get(key);
        if (jsonData == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonData, classType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패: [" + key + "]", e);
        }
    }
}