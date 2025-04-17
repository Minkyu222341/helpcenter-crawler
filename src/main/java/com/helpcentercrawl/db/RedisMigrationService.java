package com.helpcentercrawl.db;

import com.helpcentercrawl.crawler.dto.CrawlResultDto;
import com.helpcentercrawl.crawler.entity.CrawlResult;
import com.helpcentercrawl.crawler.repository.CrawlResultRepository;
import com.helpcentercrawl.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Redis에서 RDB로 데이터 마이그레이션하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMigrationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CrawlResultRepository crawlResultRepository;
    private final RedisUtil redisUtil;

    private static final String SEARCH_PATTERN = "crawl:*:";

    /**
     * 매일 자정에 Redis 데이터를 RDB로 마이그레이션
     */
    @Transactional
    @Scheduled(cron = "${scheduler.migration.cron}")
    public void migrateRedisToDatabase() {
        // 레디스 패턴에 맞는 키 조회용 날짜 선언
        LocalDate today = LocalDate.now().minusDays(1);
        String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 기존 데이터 검색용 시작일과 종료일 선언
        LocalDateTime startDate = today.atStartOfDay();
        LocalDateTime endDate = today.plusDays(1).atStartOfDay();

        // Redis에서 어제 날짜에 해당하는 키 패턴 조회
        String keyPattern = SEARCH_PATTERN + formattedDate;
        Set<String> keys = redisTemplate.keys(keyPattern);

        if (keys.isEmpty()) {
            log.info("마이그레이션할 데이터가 없습니다. 날짜: {}", formattedDate);
            return;
        }

        // 기존 데이터가 있는지 확인
        long existingCount = crawlResultRepository.countByTheDayBefore(startDate, endDate);

        if (existingCount > 0) {
            log.info("{}일자의 데이터가 {}건 존재합니다. 기존 데이터를 제거하고 새로 마이그레이션합니다.", formattedDate, existingCount);

            crawlResultRepository.deleteByExistingData(startDate, endDate);
        }

        int migratedCount = 0;
        List<CrawlResult> entities = new ArrayList<>();

        for (String key : keys) {
            try {
                CrawlResultDto redisData = redisUtil.getObject(key, CrawlResultDto.class);

                if (redisData == null) {
                    log.warn("키 {}에 대한 Redis 데이터가 없습니다.", key);
                    continue;
                }

                CrawlResult entity = CrawlResult.builder()
                        .siteCode(redisData.getSiteCode())
                        .completedCount(redisData.getCompletedCount())
                        .notCompletedCount(redisData.getNotCompletedCount())
                        .totalCount(redisData.getTotalCount())
                        .build();

                entities.add(entity);
                migratedCount++;

            } catch (Exception e) {
                log.error("데이터 마이그레이션 중 오류 발생 - 키: {}, 오류: {}", key, e.getMessage());
                log.error("오류 상세 정보:", e);
            }
        }

        // 배치 저장 수행
        if (!entities.isEmpty()) {
            crawlResultRepository.saveAll(entities);
            log.info("새 데이터 일괄 저장 완료: {}건", entities.size());
        }

        log.info("Redis에서 RDB로 데이터 마이그레이션 완료 - 총 {}건", migratedCount);
    }
}