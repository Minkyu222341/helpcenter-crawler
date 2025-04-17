package com.helpcentercrawl.dashboard.controller;

import com.helpcentercrawl.dashboard.dto.DashBoardResponseDto;
import com.helpcentercrawl.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 대시보드에 표시할 크롤링 결과 조회
     * @return List<DashBoardResponseDto>
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<DashBoardResponseDto>> getDashboardList() {
        List<DashBoardResponseDto> dashBoardList = dashboardService.getDashBoardList();

        return ResponseEntity.ok(dashBoardList);
    }

}
