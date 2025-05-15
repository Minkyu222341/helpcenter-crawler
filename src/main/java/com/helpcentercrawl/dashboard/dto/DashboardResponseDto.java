package com.helpcentercrawl.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DashboardResponseDto {
    private String siteCode;
    private String siteName;
    private Integer completedCount;
    private Integer notCompletedCount;
    private Integer totalCount;
    private Integer sequence;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;


    @QueryProjection
    public DashboardResponseDto(String siteCode, String siteName, Integer completedCount, Integer notCompletedCount, Integer totalCount, Integer sequence, LocalDateTime lastUpdatedAt) {
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
        this.sequence = sequence;
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
