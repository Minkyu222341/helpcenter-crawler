package com.helpcentercrawl.status.service;

import com.helpcentercrawl.status.config.SchedulerStatusManager;
import com.helpcentercrawl.status.dto.SchedulerStatusRequest;
import com.helpcentercrawl.status.dto.SchedulerStatusResponse;
import com.helpcentercrawl.status.dto.SiteStatusRequest;
import com.helpcentercrawl.status.dto.SiteStatusResponse;
import com.helpcentercrawl.status.entity.CrawlerStatus;
import com.helpcentercrawl.status.repository.CrawlerStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주기적으로 크롤링을 실행하는 서비스
 * 월-금요일, 08:00 ~ 18:00 사이에 3분마다 실행
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final SchedulerStatusManager statusManager;
    private final CrawlerStatusRepository crawlerStatusRepository;

    /**
     * 현재 스케줄러의 활성화 상태를 조회합니다.
     *
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

        boolean currentStatus = statusManager.setSchedulingEnabled(status);

        return SchedulerStatusResponse.toResponse(currentStatus);
    }

    /**
     * 특정 사이트의 크롤러 상태를 조회합니다.
     */
    public SiteStatusResponse getSiteStatus(String siteCode) {

        return SiteStatusResponse.toDto(getCrawler(siteCode));
    }

    /**
     * 모든 사이트의 크롤러 상태를 조회합니다.
     */
    public List<SiteStatusResponse> getAllSitesStatus() {
        List<CrawlerStatus> crawlerInfos = crawlerStatusRepository.findAll();

        return crawlerInfos.stream()
                .map(SiteStatusResponse::toDto)
                .toList();

    }

    /**
     * 특정 사이트의 크롤러 상태를 변경합니다.
     */
    @Transactional
    public SiteStatusResponse controlSiteStatus(String siteCode, SiteStatusRequest request) {
        if (request.getEnabled() == null) {
            throw new IllegalArgumentException("활성화 상태 값은 필수입니다");
        }

        CrawlerStatus crawler = getCrawler(siteCode);

        if (crawler == null) {
            throw new IllegalArgumentException("존재하지 않는 사이트 코드입니다: " + siteCode);
        }

        crawler.updateStatus(request.getEnabled());
        log.info( "{} 크롤러 상태가 {}으로 변경되었습니다.", crawler.getSiteName(), request.getEnabled() ? "활성화" : "비활성화");

        return SiteStatusResponse.toDto(crawler);
    }


    private CrawlerStatus getCrawler(String siteCode) {
        return crawlerStatusRepository.findBySiteCode(siteCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사이트 코드입니다: " + siteCode));
    }
}