package com.helpcentercrawl.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.helpcentercrawl.status.dto
 * fileName       : SiteStatusRequest
 * author         : MinKyu Park
 * date           : 25. 5. 9.
 * description    : 사이트별 크롤러 상태 변경 요청 DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 9.        MinKyu Park       최초 생성
 */
@Getter
@NoArgsConstructor
public class SiteStatusRequest {
    private Boolean enabled;
}