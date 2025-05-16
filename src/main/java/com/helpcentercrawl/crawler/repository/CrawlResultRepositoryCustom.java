package com.helpcentercrawl.crawler.repository;

import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import com.helpcentercrawl.dashboard.dto.DashboardSearchCondition;

import java.util.List;

/**
 * packageName    : com.helpcentercrawl.crawler.repository
 * fileName       : CrawlResultRepositoryCustom
 * author         : MinKyu Park
 * date           : 25. 5. 15.
 * description    : 
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 15.        MinKyu Park       최초 생성
 */
public interface CrawlResultRepositoryCustom {

    List<DashboardResponseDto> findDashboardDateList(DashboardSearchCondition condition);
}
