package com.helpcentercrawl.dashboard.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpcentercrawl.common.util.KeyGenerator;
import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DashboardResultRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final KeyGenerator keyGenerator;
    private final ObjectMapper objectMapper;

    /**
     * 특정 날짜에 해당하는 모든 사이트의 크롤링 결과 조회
     */
    public List<DashboardResponseDto> getAllCrawlResultsByDate(LocalDate date) {
        String pattern = keyGenerator.generateKey(date);
        List<DashboardResponseDto> results = new ArrayList<>();

        // Redis에서 패턴과 일치하는 모든 키 가져오기
        Set<String> keys = redisTemplate.keys(pattern);

        if (!keys.isEmpty()) {
            for (String key : keys) {
                String jsonValue = redisTemplate.opsForValue().get(key);

                if (jsonValue != null) {
                    try {
                        DashboardResponseDto dto = objectMapper.readValue(jsonValue, DashboardResponseDto.class);
                        results.add(dto);
                    } catch (JsonProcessingException e) {
                        log.error("JSON 역직렬화 중 오류 발생 (키: {}): {}", key, e.getMessage(), e);
                    }
                }
            }
        }

        return results;
    }
}
