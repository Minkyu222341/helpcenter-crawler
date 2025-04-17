package com.helpcentercrawl.dashboard.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DashBoardResponseDto {
    private String siteCode;
    private String siteName;
    private Integer completedCount;
    private Integer notCompletedCount;
    private Integer totalCount;
    private LocalDate crawlDate;
}
