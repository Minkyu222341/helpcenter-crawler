package com.helpcentercrawl.dashboard.service;

import com.helpcentercrawl.crawler.repository.CrawlResultRepository;
import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import com.helpcentercrawl.dashboard.dto.DashboardSearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CrawlResultRepository crawlResultRepository;

    /**
     * 대시보드에 표시할 크롤링 결과 조회
     *
     * @return List<DashBoardResponseDto>
     */
    public List<DashboardResponseDto> getDashBoardList(DashboardSearchCondition condition) {

        return crawlResultRepository.findDashboardDateList(condition);
    }
}