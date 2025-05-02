package com.helpcentercrawl.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.helpcentercrawl.scheduler.dto
 * fileName       : SchedulerStatusResponse
 * author         : MinKyu Park
 * date           : 25. 5. 2.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 2.        MinKyu Park       최초 생성
 */
@Getter
@AllArgsConstructor
public class SchedulerStatusResponse {
    private boolean status;


    public static SchedulerStatusResponse toResponse(boolean currentStatus) {
        return new SchedulerStatusResponse(currentStatus);
    }
}
