package com.helpcentercrawl.scheduler.job;

import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.scheduler.config.SchedulerStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduling {

    private final SchedulerStatusManager statusManager;
    private final List<SiteCrawler> crawlers;

    /**
     * 3분마다 크롤링 실행 (월-금요일, 08:00 ~ 18:00 사이에만)
     */
    @Scheduled(cron = "${scheduler.crawler.cron}")
    public void runCrawlers() {

        if (!statusManager.isSchedulingEnabled()) {
            log.info("크롤링 스케줄링이 비활성화되어 있어 작업을 건너뜁니다.");
            return;
        }

        log.info("크롤링 시작: {} 개 사이트", crawlers.size());

        for (SiteCrawler crawler : crawlers) {
            try {
                crawler.crawl();
            } catch (Exception e) {
                log.error("{} 크롤링 중 오류 발생: {}", crawler.getSiteName(), e.getMessage());
                log.error("오류 상세 정보:", e);
            }
        }

        log.info("크롤링 완료");
    }
}
