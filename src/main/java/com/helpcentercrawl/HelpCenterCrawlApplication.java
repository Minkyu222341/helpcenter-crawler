package com.helpcentercrawl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // 스케줄링 기능 활성화
@EnableJpaAuditing
public class HelpCenterCrawlApplication {

//    TODO : 로컬에서 DB 설정 제외하고 실행하기 위해서 해야할 조치
//           1. RedisConfig의 @Configuration 비활성화
//           2. AbsstractCrawler의 RedisMaigration 실행부 비활성화
//           3. RedisMigrationService 클래스 비활성화

    public static void main(String[] args) {
        SpringApplication.run(HelpCenterCrawlApplication.class, args);
    }
}