package com.helpcentercrawl.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.helpcentercrawl.common.util
 * fileName       : RedisUtil
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    : Redis에서 객체를 조회하기 위한 유틸리티 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-04-18        MinKyu Park       최초 생성
 */

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