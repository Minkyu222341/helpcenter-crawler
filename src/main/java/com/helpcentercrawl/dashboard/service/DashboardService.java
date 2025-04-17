package com.helpcentercrawl.dashboard.service;

import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import com.helpcentercrawl.dashboard.repository.DashboardResultRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardResultRedisRepository dashboardResultRedisRepository;

    /**
     * 대시보드에 표시할 크롤링 결과 조회
     * @return List<DashBoardResponseDto>
     */
    public List<DashboardResponseDto> getDashBoardList() {

        return dashboardResultRedisRepository.getAllCrawlResultsByDate(LocalDate.now());
    }

}