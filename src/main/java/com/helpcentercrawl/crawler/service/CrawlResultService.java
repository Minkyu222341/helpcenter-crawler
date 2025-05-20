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
    private static final int BATCH_SIZE = 500;

    @Transactional
    public void processCrawlResults(List<CrawlResult> crawledRequests, String siteCode, String siteName) {
        if (siteCode == null || siteCode.trim().isEmpty()) {
            log.error("사이트 코드가 null 또는 비어 있습니다. 크롤링 결과 처리를 중단합니다.");
            return;
        }
        if (siteName == null) {
            log.warn("사이트 이름이 null입니다. 사이트 코드({})를 사용합니다.", siteCode);
            siteName = siteCode;
        }
        if (crawledRequests.isEmpty()) {
            log.info("{} - 저장할 크롤링 결과가 없습니다.", siteName);
            return;
        }

        List<CrawlResult> existingData = crawlResultRepository.findBySiteCode(siteCode);

        Map<String, CrawlResult> existingMap = new HashMap<>();

        for (CrawlResult existing : existingData) {
            String key = createKey(existing.getSiteCode(), existing.getTitle(), existing.getRequestDate());
            existingMap.put(key, existing);
        }

        List<CrawlResult> newDataList = new ArrayList<>();
        int updatedCount = 0;

        for (CrawlResult newResult : crawledRequests) {
            String key = createKey(newResult.getSiteCode(), newResult.getTitle(), newResult.getRequestDate());

            CrawlResult existing = existingMap.get(key);

            if (existing != null) {
                if (existing.getStatus() != null && newResult.getStatus() != null &&
                        !existing.getStatus().equals(newResult.getStatus())) {
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


    private String createKey(String siteCode, String title, Object requestDate) {
        return Optional.ofNullable(siteCode).orElse("") +
                "_" +
                Optional.ofNullable(title).orElse("") +
                "_" +
                Optional.ofNullable(requestDate).orElse("");
    }

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
            List<CrawlResult> savedResults = new ArrayList<>();
            for (int i = 0; i < crawlResults.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, crawlResults.size());
                List<CrawlResult> batch = crawlResults.subList(i, end);
                savedResults.addAll(crawlResultRepository.saveAll(batch));
                log.debug("배치 저장 완료: {}/{}", end, crawlResults.size());
            }
            return savedResults;
        } catch (Exception e) {
            log.error("DB에 크롤링 결과 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}