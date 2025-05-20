package com.helpcentercrawl.crawler.repository;

import com.helpcentercrawl.crawler.entity.enums.RequestStatus;
import com.helpcentercrawl.dashboard.dto.DashboardResponseDto;
import com.helpcentercrawl.dashboard.dto.DashboardSearchCondition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static com.helpcentercrawl.crawler.entity.QCrawlResult.crawlResult;
import static com.helpcentercrawl.status.entity.QCrawlerStatus.crawlerStatus;

/**
 * packageName    : com.helpcentercrawl.crawler.repository
 * fileName       : CrawlResultRepositoryImpl
 * author         : MinKyu Park
 * date           : 25. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 15.        MinKyu Park       최초 생성
 */
@RequiredArgsConstructor
public class CrawlResultRepositoryImpl implements CrawlResultRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<DashboardResponseDto> findDashboardDateList(DashboardSearchCondition condition) {

        return queryFactory
                .select(Projections.constructor(DashboardResponseDto.class,
                        crawlerStatus.siteCode,
                        crawlerStatus.siteName,
                        new CaseBuilder()
                                .when(crawlResult.status.eq(RequestStatus.SUCCESS))
                                .then(1L)
                                .otherwise(0L).sum().intValue(),
                        new CaseBuilder()
                                .when(crawlResult.status.eq(RequestStatus.WAIT))
                                .then(1L)
                                .otherwise(0L).sum().intValue(),
                        crawlResult.id.count().intValue(),
                        crawlerStatus.viewSequence,
                        crawlerStatus.crawledAt))
                .from(crawlerStatus)
                .innerJoin(crawlResult).on(crawlerStatus.siteCode.eq(crawlResult.siteCode))
                .where(
                        startDateGoe(condition.getStartDate()),
                        endDateLoe(condition.getEndDate())
                )
                .groupBy(crawlerStatus.siteCode, crawlerStatus.siteName, crawlerStatus.viewSequence,crawlerStatus.crawledAt)
                .orderBy(crawlerStatus.viewSequence.asc())
                .fetch();
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        return startDate != null ? crawlResult.requestDate.goe(startDate) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate) {
        return endDate != null ? crawlResult.requestDate.loe(endDate) : null;
    }
}