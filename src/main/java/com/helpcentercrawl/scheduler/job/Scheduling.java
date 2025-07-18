package com.helpcentercrawl.scheduler.job;

import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.status.config.SchedulerStatusManager;
import com.helpcentercrawl.status.entity.CrawlerStatus;
import com.helpcentercrawl.status.repository.CrawlerStatusRepository;
import com.helpcentercrawl.status.service.SchedulerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.helpcentercrawl.scheduler.job
 * fileName       : Scheduling
 * author         : MinKyu Park
 * date           : 25. 5. 2.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 2.        MinKyu Park       최초 생성
 * 25. 5. 9.        MinKyu Park       사이트별 상태 확인 로직 추가
 * 25. 5. 13.       MinKyu Park       크롤링 실행 시간 측정 로직 추가
 * 25. 5. 19.       MinKyu Park       크롤러 조회 성능 개선을 위한 Map 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduling {

    private final SchedulerStatusManager statusManager;
    private final SchedulerService schedulerService;
    private final CrawlerStatusRepository crawlerStatusRepository;
    private final List<SiteCrawler> crawlers;

    // 크롤러 사이트 코드를 키로 사용하는 맵 추가
    private Map<String, SiteCrawler> crawlerMap;

    /**
     * 애플리케이션 시작 시 크롤러 맵 초기화
     */
    @PostConstruct
    public void initCrawlerMap() {
        crawlerMap = new HashMap<>();

        for (SiteCrawler crawler : crawlers) {
            crawlerMap.put(crawler.getSiteCode(), crawler);
        }

        log.info("크롤러 맵 초기화 완료: {} 개의 크롤러 등록됨", crawlerMap.size());
    }

    /**
     * 3분마다 크롤링 실행 (월-금요일, 08:00 ~ 18:00 사이에만)
     */
    @Scheduled(cron = "${scheduler.crawler.cron}")
//    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    public void runCrawlers() {
        Instant totalStart = Instant.now();

        if (!statusManager.isSchedulingEnabled()) {
            log.info("크롤링 스케줄링이 비활성화되어 있어 작업을 건너뜁니다.");
            return;
        }

        List<CrawlerStatus> statuses = crawlerStatusRepository.findAll();

        List<String> successfulSiteCodes = new ArrayList<>(); // 성공한 사이트 코드 모음

        log.info("크롤링 시작: {} 개 사이트", statuses.size());
        int enabledCount = 0;

        for (CrawlerStatus status : statuses) {
            String siteCode = status.getSiteCode();
            String siteName = status.getSiteName();

            if (!status.isEnabled()) {
                log.info("{} 크롤러가 비활성화되어 있어 건너뜁니다.", siteName);
                continue;
            }

            SiteCrawler crawler = crawlerMap.get(siteCode);

            if (crawler == null) {
                log.warn("{} 사이트에 대한 크롤러 구현체를 찾을 수 없습니다.", siteName);
                continue;
            }

            enabledCount++;

            Instant siteStart = Instant.now();
            boolean success = false;

            try {
                crawler.crawl();
                success = true;
            } catch (Exception e) {
                log.error("{} 크롤링 중 오류 발생: {}", siteName, e.getMessage());
                log.error("오류 상세 정보:", e);
            } finally {
                Instant siteEnd = Instant.now();
                Duration siteDuration = Duration.between(siteStart, siteEnd);

                if (success) {
                    log.info("{} 크롤링 완료: {}초", siteName, siteDuration.toSeconds());
                    successfulSiteCodes.add(siteCode); // 성공한 사이트 코드 추가
                } else {
                    log.info("{} 크롤링 실패 (처리 시간: {}초)", siteName, siteDuration.toSeconds());
                }
            }
        }

        if (!successfulSiteCodes.isEmpty()) {
            schedulerService.updateCrawledAtBatch(successfulSiteCodes);
        }

        Instant totalEnd = Instant.now();
        Duration totalDuration = Duration.between(totalStart, totalEnd);
        log.info("크롤링 작업 완료 - 활성화된 크롤러: {}/{}, 총 실행 시간: {}초", enabledCount, statuses.size(), totalDuration.toSeconds());
    }
}