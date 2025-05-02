package com.helpcentercrawl.scheduler.service;

import com.helpcentercrawl.scheduler.config.SchedulerStatusManager;
import com.helpcentercrawl.scheduler.dto.SchedulerStatusRequest;
import com.helpcentercrawl.scheduler.dto.SchedulerStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 주기적으로 크롤링을 실행하는 서비스
 * 월-금요일, 08:00 ~ 18:00 사이에 3분마다 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final SchedulerStatusManager statusManager;

    /**
     * 현재 스케줄러의 활성화 상태를 조회합니다.
     * @return 현재 스케줄러 상태
     */
    public SchedulerStatusResponse getSchedulerStatus() {
        boolean currentStatus = statusManager.isSchedulingEnabled();

        return SchedulerStatusResponse.toResponse(currentStatus);
    }

    /**
     * 스케줄러의 활성화 상태를 제어합니다.
     *
     * @param request 상태 변경 요청 정보 (status: true/false)
     * @return 변경된 스케줄러 상태
     */
    public SchedulerStatusResponse controlSchedulerStatus(SchedulerStatusRequest request) {
        Boolean status = request.getStatus();

        if (status == null) {
            log.warn("상태 값이 제공되지 않았습니다.");
            throw new IllegalArgumentException("상태 값은 필수입니다");
        }

        boolean currentStatus = statusManager.toggleSchedulingEnabled(status);

        return SchedulerStatusResponse.toResponse(currentStatus);
    }


}