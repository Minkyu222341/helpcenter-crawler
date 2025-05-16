package com.helpcentercrawl.status.controller;

import com.helpcentercrawl.status.dto.SchedulerStatusRequest;
import com.helpcentercrawl.status.dto.SchedulerStatusResponse;
import com.helpcentercrawl.status.dto.SiteStatusRequest;
import com.helpcentercrawl.status.dto.SiteStatusResponse;
import com.helpcentercrawl.status.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName    : com.helpcentercrawl.status.controller
 * fileName       : ScheduleController
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    : 크롤링 스케줄 상태 관리 컨트롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 * 25. 5. 9.         MinKyu Park       사이트별 상태 관리 기능 추가
 * 25. 5. 12.        MinKyu Park       REST API 설계 원칙에 따른 메소드 및 엔드포인트 개선
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final SchedulerService schedulerService;

    /**
     * 현재 스케줄러의 활성화 상태를 조회합니다.
     */
    @GetMapping("/status")
    public ResponseEntity<SchedulerStatusResponse> getScheduleStatus() {
        return ResponseEntity.ok(schedulerService.getSchedulerStatus());
    }

    /**
     * 스케줄러의 활성화 상태를 제어합니다.
     *
     * @param request 상태 변경 요청 정보 (status: true/false)
     */
    @PutMapping("/status")
    public ResponseEntity<SchedulerStatusResponse> updateScheduleStatus(@RequestBody SchedulerStatusRequest request) {
        SchedulerStatusResponse response = schedulerService.controlSchedulerStatus(request);
        log.info("스케줄러 상태 변경 완료: {}", response.isStatus());
        return ResponseEntity.ok(response);
    }

    /**
     * 사이트별 크롤러 상태 조회
     *
     * @param siteCode 사이트 코드
     */
    @GetMapping("/sites/{siteCode}/status")
    public ResponseEntity<SiteStatusResponse> getSiteStatus(@PathVariable String siteCode) {
        return ResponseEntity.ok(schedulerService.getSiteStatus(siteCode));
    }

    /**
     * 모든 사이트의 크롤러 상태 조회
     */
    @GetMapping("/sites/status")
    public ResponseEntity<List<SiteStatusResponse>> getAllSitesStatus() {
        return ResponseEntity.ok(schedulerService.getAllSitesStatus());
    }

    /**
     * 사이트별 크롤러 상태 변경
     *
     * @param siteCode 사이트 코드
     * @param request  상태 변경 요청 정보 (enabled: true/false)
     */
    @PutMapping("/sites/{siteCode}/status")
    public ResponseEntity<SiteStatusResponse> updateSiteStatus(
            @PathVariable("siteCode") String siteCode,
            @RequestBody SiteStatusRequest request) {

        return ResponseEntity.ok(schedulerService.controlSiteStatus(siteCode, request));
    }
}