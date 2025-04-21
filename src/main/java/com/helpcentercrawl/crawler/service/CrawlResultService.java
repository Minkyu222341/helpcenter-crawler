package com.helpcentercrawl.crawler.service;

import com.helpcentercrawl.crawler.dto.CrawlResultDto;
import com.helpcentercrawl.crawler.dto.CrawlSaveDto;
import com.helpcentercrawl.crawler.repository.CrawlResultRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlResultService {

    private final CrawlResultRedisRepository crawlResultRedisRepository;

    /**
     * 크롤링 결과를 Redis에 저장
     */
    public void saveCrawlResult(CrawlSaveDto crawlSaveDto) {
        try {
            crawlResultRedisRepository.saveCrawlResult(crawlSaveDto);
            log.info("Redis에 크롤링 결과 저장 완료: {}", crawlSaveDto.getSiteName());
        } catch (Exception e) {
            log.error("Redis에 크롤링 결과 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 사이트와 날짜에 해당하는 크롤링 결과 조회
     */
    public CrawlResultDto getCrawlResult(String siteCode, LocalDate date) {
        try {
            return crawlResultRedisRepository.getCrawlResult(siteCode, date);
        }catch (Exception e) {
            log.error("Redis에서 크롤링 결과 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }

    }
}