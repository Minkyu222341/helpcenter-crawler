package com.helpcentercrawl.service;

import com.helpcentercrawl.crawler.domain.SiteCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final List<SiteCrawler> crawlers;

    /**
     * 30초마다 크롤링 실행
     */
    @Scheduled(fixedDelayString = "${scheduler.crawler.fixed-delay:30000}")
    public void runCrawlers() {
        log.info("주기적인 크롤링 시작: {} 개 사이트", crawlers.size());

        for (SiteCrawler crawler : crawlers) {
            try {
                crawler.crawl();
            } catch (Exception e) {
                log.error("{} 크롤링 중 오류 발생: {}", crawler.getSiteName(), e.getMessage());
                log.error("오류 상세 정보:", e);
            }
        }

        log.info("주기적인 크롤링 완료");
    }
}