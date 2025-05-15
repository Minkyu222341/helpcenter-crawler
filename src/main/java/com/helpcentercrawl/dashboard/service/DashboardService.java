package com.helpcentercrawl.dashboard.service;

import com.helpcentercrawl.crawler.entity.CrawlResult;
import com.helpcentercrawl.crawler.entity.enums.RequestStatus;
import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.crawler.repository.CrawlResultRepository;
import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CrawlResultRepository crawlResultRepository;
    private final ApplicationContext applicationContext;

    /**
     * 대시보드에 표시할 크롤링 결과 조회
     * @return List<DashBoardResponseDto>
     */
    public List<DashboardResponseDto> getDashBoardList() {
        // 1. 모든 SiteCrawler 구현체 가져오기
        Map<String, Integer> siteSequences = getSiteSequences();

        // 2. 모든 크롤링 결과 조회
        List<CrawlResult> crawlResults = crawlResultRepository.findAll();

        // 3. 사이트 코드별로 그룹화
        Map<String, List<CrawlResult>> groupedResults = crawlResults.stream()
                .collect(Collectors.groupingBy(CrawlResult::getSiteCode));

        // 4. 그룹별로 DTO 생성
        List<DashboardResponseDto> dashboardList = new ArrayList<>();

        for (Map.Entry<String, List<CrawlResult>> entry : groupedResults.entrySet()) {
            String siteCode = entry.getKey();
            List<CrawlResult> results = entry.getValue();

            if (results.isEmpty()) {
                continue;
            }

            String siteName = results.get(0).getSiteName();

            long completedCount = results.stream()
                    .filter(r -> RequestStatus.SUCCESS.equals(r.getStatus()))
                    .count();

            long notCompletedCount = results.stream()
                    .filter(r -> RequestStatus.WAIT.equals(r.getStatus()))
                    .count();

            int totalCount = results.size();

            // 가장 최근 업데이트 시간
            LocalDateTime lastUpdatedAt = results.stream()
                    .map(CrawlResult::getUpdatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            // SiteCrawler에서 Sequence 정보 가져오기
            Integer sequence = siteSequences.getOrDefault(siteCode, 999); // 기본값 설정

            DashboardResponseDto dto = DashboardResponseDto.builder()
                    .siteCode(siteCode)
                    .siteName(siteName)
                    .completedCount((int) completedCount)
                    .notCompletedCount((int) notCompletedCount)
                    .totalCount(totalCount)
                    .sequence(sequence)
                    .lastUpdatedAt(lastUpdatedAt)
                    .build();

            dashboardList.add(dto);
        }

        return dashboardList;
    }

    /**
     * 모든 SiteCrawler 구현체에서 사이트 코드와 시퀀스 매핑 정보 가져오기
     */
    private Map<String, Integer> getSiteSequences() {
        Map<String, SiteCrawler> crawlers = applicationContext.getBeansOfType(SiteCrawler.class);

        return crawlers.values().stream()
                .collect(Collectors.toMap(
                        SiteCrawler::getSiteCode,
                        SiteCrawler::getSequence
                ));
    }

}