package com.helpcentercrawl.crawler.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * packageName    : com.helpcentercrawl.crawler.dto
 * fileName       : CrawlSaveDto
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    : 크롤링 결과를 Redis에 저장하기 위한 DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-04-18        MinKyu Park       최초 생성
 */
@Getter
@NoArgsConstructor
public class CrawlSaveDto {
    private String siteCode;
    private String siteName;
    private Integer completedCount;
    private Integer notCompletedCount;
    private Integer totalCount;
    private Integer sequence;
    private LocalDate crawlDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    @Builder
    public CrawlSaveDto(String siteCode, String siteName, Integer completedCount, Integer notCompletedCount, Integer totalCount, Integer sequence, LocalDate crawlDate, LocalDateTime lastUpdatedAt) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
        this.sequence = sequence;
        this.crawlDate = crawlDate;
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
