package com.helpcentercrawl.scheduler.job;

import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.status.config.CrawlerStatusManager;
import com.helpcentercrawl.status.config.SchedulerStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduling {

    private final SchedulerStatusManager statusManager;
    private final CrawlerStatusManager crawlerStatusManager;
    private final List<SiteCrawler> crawlers;

    /**
     * 3분마다 크롤링 실행 (월-금요일, 08:00 ~ 18:00 사이에만)
     */
    @Scheduled(cron = "${scheduler.crawler.cron}")
//    @Scheduled(fixedDelay = 180000, initialDelay = 1000)
    public void runCrawlers() {
        Instant totalStart = Instant.now();

        if (!statusManager.isSchedulingEnabled()) {
            log.info("크롤링 스케줄링이 비활성화되어 있어 작업을 건너뜁니다.");
            return;
        }

        log.info("크롤링 시작: {} 개 사이트", crawlers.size());
        int enabledCount = 0;

        for (SiteCrawler crawler : crawlers) {
            String siteCode = crawler.getSiteCode();
            String siteName = crawler.getSiteName();

            // 사이트별 활성화 상태 확인
            if (!crawlerStatusManager.isSiteEnabled(siteCode)) {
                log.info("{} 크롤러가 비활성화되어 있어 건너뜁니다.", siteName);
                continue;
            }

            enabledCount++;


            try {
                crawler.crawl();
            } catch (Exception e) {
                log.error("{} 크롤링 중 오류 발생: {}", siteName, e.getMessage());
                log.error("오류 상세 정보:", e);
            }
        }

        Instant totalEnd = Instant.now();
        Duration totalDuration = Duration.between(totalStart, totalEnd);

        log.info("크롤링 작업 완료 - 활성화된 크롤러: {}/{}, 총 실행 시간: {}초", enabledCount, crawlers.size(), totalDuration.toSeconds());
    }
}