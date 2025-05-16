package com.helpcentercrawl.crawler.service;

import com.helpcentercrawl.crawler.entity.CrawlResult;
import com.helpcentercrawl.crawler.repository.CrawlResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlResultService {

    private final CrawlResultRepository crawlResultRepository;

    /**
     * 크롤링 결과 목록을 DB에 저장
     *
     * @param crawlResults 저장할 크롤링 결과 목록
     * @return 저장된 크롤링 결과 목록
     */
    @Transactional
    public List<CrawlResult> saveCrawlResults(List<CrawlResult> crawlResults) {
        if (crawlResults == null || crawlResults.isEmpty()) {
            log.warn("저장할 크롤링 결과가 없습니다.");
            return Collections.emptyList();
        }

        try {
            return crawlResultRepository.saveAll(crawlResults);
        } catch (Exception e) {
            log.error("DB에 크롤링 결과 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 특정 사이트의 기존 크롤링 결과 조회 (중복 확인용)
     *
     * @param siteCode 사이트 코드
     * @return 해당 사이트의 모든 크롤링 결과
     */
    @Transactional(readOnly = true)
    public List<CrawlResult> findExistingResults(String siteCode) {
        try {
            return crawlResultRepository.findBySiteCode(siteCode);
        } catch (Exception e) {
            log.error("기존 크롤링 결과 조회 중 오류 발생: 사이트={}, 오류={}",
                    siteCode, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}