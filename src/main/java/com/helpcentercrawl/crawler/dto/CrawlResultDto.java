package com.helpcentercrawl.crawler.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * packageName    : com.helpcentercrawl.crawler.dto
 * fileName       : CrawlResultDto
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    : 크롤링 결과를 Redis에서 조회하기 위한 DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-04-18        MinKyu Park       최초 생성
 */

@Getter
@NoArgsConstructor
public class CrawlResultDto {
    private String siteCode;
    private String siteName;
    private Integer completedCount;
    private Integer notCompletedCount;
    private Integer totalCount;
    private LocalDate crawlDate;

    @Builder
    public CrawlResultDto(String siteCode, String siteName, Integer completedCount,
                          Integer notCompletedCount, Integer totalCount, LocalDate crawlDate) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
        this.crawlDate = crawlDate != null ? crawlDate : LocalDate.now();
    }

}