package com.helpcentercrawl.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.helpcentercrawl.dto.CrawlResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CrawlResultRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "crawl:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final long EXPIRATION_DAYS = 30; // 30일 후 만료

    public CrawlResultRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 크롤링 결과를 Redis에 저장
     * 키 형식: crawl:result:{사이트코드}:{yyyyMMdd}
     */
    public void saveCrawlResult(CrawlResultDto crawlResultDto) {
        try {
            String key = generateKey(crawlResultDto.getSiteCode(), crawlResultDto.getCrawlDate());
            String jsonValue = objectMapper.writeValueAsString(crawlResultDto);
            redisTemplate.opsForValue().set(key, jsonValue);
            redisTemplate.expire(key, EXPIRATION_DAYS, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Redis 저장 실패", e);
        }
    }

    /**
     * 특정 사이트와 날짜에 해당하는 크롤링 결과 조회
     */
    public CrawlResultDto getCrawlResult(String siteCode, LocalDate date) {
        String key = generateKey(siteCode, date);
        String jsonValue = redisTemplate.opsForValue().get(key);

        if (jsonValue == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonValue, CrawlResultDto.class);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Redis 조회 실패", e);
        }
    }

    /**
     * 키 생성 메서드
     */
    private String generateKey(String siteCode, LocalDate date) {
        return KEY_PREFIX + siteCode + ":" + date.format(DATE_FORMATTER);
    }
}