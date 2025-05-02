package com.helpcentercrawl.scheduler.controller;

import com.helpcentercrawl.scheduler.dto.SchedulerStatusRequest;
import com.helpcentercrawl.scheduler.dto.SchedulerStatusResponse;
import com.helpcentercrawl.scheduler.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : com.helpcentercrawl.scheduler.controller
 * fileName       : ScheduleController
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final SchedulerService schedulerService;


    /**
     * 현재 스케줄러의 활성화 상태를 조회합니다.
     * @return 현재 스케줄러 상태 정보를 포함한 ResponseEntity
     */
    @GetMapping("/status")
    public ResponseEntity<SchedulerStatusResponse> getScheduleStatus() {

        return ResponseEntity.ok(schedulerService.getSchedulerStatus());
    }

    /**
     * 스케줄러의 활성화 상태를 제어합니다.
     *
     * @param request 상태 변경 요청 정보 (status: true/false)
     * @return 변경된 스케줄러 상태 정보를 포함한 ResponseEntity
     */
    @PostMapping("/control")
    public ResponseEntity<SchedulerStatusResponse> controlSchedule(@RequestBody SchedulerStatusRequest request) {

        return ResponseEntity.ok(schedulerService.controlSchedulerStatus(request));
    }

}