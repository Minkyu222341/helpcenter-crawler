package com.helpcentercrawl.crawler.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helpcentercrawl.common.util.KeyGenerator;
import com.helpcentercrawl.crawler.dto.CrawlResultDto;
import com.helpcentercrawl.crawler.dto.CrawlSaveDto;

import com.helpcentercrawl.crawler.model.CrawlRedisModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * packageName    : com.helpcentercrawl.crawler.repository
 * fileName       : CrawlResultRedisRepository
 * author         : MinKyu Park
 * date           : 25. 4. 21.
 * description    : Redis의 데이터를 저장하고 조회하는 Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 21.        MinKyu Park       최초 생성
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class CrawlResultRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final KeyGenerator keyGenerator;
    private static final long EXPIRATION_DAYS = 30; // 30일 후 만료

    /**
     * 크롤링 결과를 Redis에 저장
     * 키 형식: crawl:result:{사이트코드}:{yyyyMMdd}
     */
    public void saveCrawlResult(CrawlSaveDto crawlSaveDto) {
        try {
            String key = keyGenerator.generateKey(crawlSaveDto.getSiteCode(), crawlSaveDto.getCrawlDate());

            CrawlRedisModel redisModel = CrawlRedisModel.fromSaveDto(crawlSaveDto);

            String jsonValue = objectMapper.writeValueAsString(redisModel);

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
        String key = keyGenerator.generateKey(siteCode, date);
        String jsonValue = redisTemplate.opsForValue().get(key);

        if (jsonValue == null) {
            return null;
        }

        try {
            CrawlRedisModel redisModel = objectMapper.readValue(jsonValue, CrawlRedisModel.class);
            return redisModel.toResultDto();
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Redis 조회 실패", e);
        }
    }


}