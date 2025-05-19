package com.helpcentercrawl.crawler.service;

import com.helpcentercrawl.crawler.entity.CrawlResult;
import com.helpcentercrawl.crawler.repository.CrawlResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
            log.error("기존 크롤링 결과 조회 중 오류 발생: 사이트={}, 오류={}", siteCode, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void processCrawlResults(List<CrawlResult> crawledRequests, String siteCode, String siteName) {
        if (crawledRequests.isEmpty()) {
            log.info("{} - 저장할 크롤링 결과가 없습니다.", siteName);
            return;
        }

        // 기존 데이터 조회
        List<CrawlResult> existingData = findExistingResults(siteCode);

        Map<String, CrawlResult> existingMap = new HashMap<>();
        for (CrawlResult existing : existingData) {
            String key = existing.getSiteCode() + "_" + existing.getTitle() + "_" + existing.getRequestDate();
            existingMap.put(key, existing);
        }

        List<CrawlResult> newDataList = new ArrayList<>();
        int updatedCount = 0;

        for (CrawlResult newResult : crawledRequests) {
            String key = newResult.getSiteCode() + "_" + newResult.getTitle() + "_" + newResult.getRequestDate();

            CrawlResult existing = existingMap.get(key);

            if (existing != null) {
                if (!existing.getStatus().equals(newResult.getStatus())) {
                    boolean updated = existing.updateStatus(newResult.getStatus());
                    if (updated) {
                        updatedCount++;
                    }
                }
            } else {
                newDataList.add(newResult);
            }
        }

        if (!newDataList.isEmpty()) {
            int savedCount = saveCrawlResults(newDataList).size();
            log.info("{} - DB에 새로운 크롤링 결과 {}건이 저장되었습니다.", siteName, savedCount);
        } else {
            log.info("{} - 새로운 데이터가 없습니다.", siteName);
        }

        if (updatedCount > 0) {
            log.info("{} - 상태값이 변경된 크롤링 결과 {}건이 업데이트되었습니다.", siteName, updatedCount);
        }
    }
}