package com.helpcentercrawl.crawler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.helpcentercrawl.crawler.dto.CrawlResultDto;
import com.helpcentercrawl.crawler.dto.CrawlSaveDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * packageName    : com.helpcentercrawl.crawler.model
 * fileName       : CrawlRedisModel
 * author         : MinKyu Park
 * date           : 25. 4. 21.
 * description    : Redis 저장 및 조회용 내부 직렬화 모델 CrawlSaveDto ↔ CrawlRedisModel ↔ CrawlResultDto
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 21.        MinKyu Park       최초 생성
 */
@Getter
@NoArgsConstructor
public class CrawlRedisModel {
    private String siteCode;
    private String siteName;
    private Integer completedCount;
    private Integer notCompletedCount;
    private Integer totalCount;
    private LocalDate crawlDate;
    private Integer sequence;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    @Builder
    public CrawlRedisModel(String siteCode, String siteName, Integer completedCount, Integer notCompletedCount, Integer totalCount, LocalDate crawlDate, Integer sequence, LocalDateTime lastUpdatedAt) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
        this.crawlDate = crawlDate;
        this.sequence = sequence;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public static CrawlRedisModel fromSaveDto(CrawlSaveDto dto) {
        return CrawlRedisModel.builder()
                .siteCode(dto.getSiteCode())
                .siteName(dto.getSiteName())
                .completedCount(dto.getCompletedCount())
                .notCompletedCount(dto.getNotCompletedCount())
                .totalCount(dto.getTotalCount())
                .crawlDate(dto.getCrawlDate())
                .sequence(dto.getSequence())
                .lastUpdatedAt(dto.getLastUpdatedAt())
                .build();
    }

    public CrawlResultDto toResultDto() {
        return CrawlResultDto.builder()
                .siteCode(this.siteCode)
                .siteName(this.siteName)
                .completedCount(this.completedCount)
                .notCompletedCount(this.notCompletedCount)
                .totalCount(this.totalCount)
                .sequence(this.sequence)
                .crawlDate(this.crawlDate)
                .build();
    }
}
