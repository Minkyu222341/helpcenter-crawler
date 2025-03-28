package com.helpcentercrawl;

import com.helpcentercrawl.crawler.domain.SiteCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@Slf4j
public class HelpCenterCrawlApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelpCenterCrawlApplication.class, args);
    }

    @Bean
    public CommandLineRunner runCrawlers(List<SiteCrawler> crawlers) {
        return args -> {
            log.info("총 {}개의 사이트 크롤링을 시작합니다.", crawlers.size());

            for (SiteCrawler crawler : crawlers) {
                try {
                    crawler.crawl();
                } catch (Exception e) {
                    log.error("{} 크롤링 중 오류 발생: {}", crawler.getSiteName(), e.getMessage());
                    log.error("오류 상세 정보:", e);
                }
            }

            log.info("모든 사이트 크롤링이 완료되었습니다.");
            System.exit(0);
        };
    }
}
