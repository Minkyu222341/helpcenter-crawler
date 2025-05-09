package com.helpcentercrawl.status.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * packageName    : com.helpcentercrawl.status.dto
 * fileName       : SiteStatusResponse
 * author         : MinKyu Park
 * date           : 25. 5. 9.
 * description    : 사이트별 크롤러 상태 응답 DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 9.        MinKyu Park       최초 생성
 */
@Getter
@Builder
public class SiteStatusResponse {
    private String siteCode;
    private String siteName;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}