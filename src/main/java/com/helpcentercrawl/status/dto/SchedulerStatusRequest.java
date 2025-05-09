package com.helpcentercrawl.status.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.helpcentercrawl.scheduler.dto
 * fileName       : SchedulerStatusRequest
 * author         : MinKyu Park
 * date           : 25. 5. 2.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 2.        MinKyu Park       최초 생성
 */
@Getter
@NoArgsConstructor
public class SchedulerStatusRequest {
    private Boolean status;
}