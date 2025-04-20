package com.helpcentercrawl.crawler.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.helpcentercrawl.crawler.dto
 * fileName       : CrawlSaveDto
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    :
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
	private LocalDate crawlDate;
	private String lastUpdatedAt;

	@Builder
	public CrawlSaveDto(String siteCode, String siteName, Integer completedCount, Integer notCompletedCount,
		Integer totalCount, LocalDate crawlDate, String lastUpdatedAt) {
		this.siteCode = siteCode;
		this.siteName = siteName;
		this.completedCount = completedCount;
		this.notCompletedCount = notCompletedCount;
		this.totalCount = totalCount;
		this.crawlDate = crawlDate;
		this.lastUpdatedAt = lastUpdatedAt;
	}
}
