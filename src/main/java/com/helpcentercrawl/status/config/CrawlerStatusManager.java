package com.helpcentercrawl.status.config;

import com.helpcentercrawl.status.entity.CrawlerStatus;
import com.helpcentercrawl.status.repository.CrawlerStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.helpcentercrawl.status.config
 * fileName       : CrawlerStatusManager
 * author         : MinKyu Park
 * date           : 25. 5. 9.
 * description    : 크롤러 상태를 관리하는 컴포넌트
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 9.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlerStatusManager {
    private final CrawlerStatusRepository repository;

    /**
     * 특정 사이트의 크롤링 활성화 상태 설정
     */
    @Transactional
    public boolean setSiteEnabled(String siteCode, String siteName, boolean enabled) {
        Optional<CrawlerStatus> statusOpt = repository.findBySiteCode(siteCode);

        CrawlerStatus status;
        if (statusOpt.isPresent()) {
            status = statusOpt.get();

            status = CrawlerStatus.builder()
                    .id(status.getId())
                    .siteCode(status.getSiteCode())
                    .siteName(status.getSiteName())
                    .enabled(enabled)
                    .build();
        } else {
            status = CrawlerStatus.builder()
                    .siteCode(siteCode)
                    .siteName(siteName)
                    .enabled(enabled)
                    .build();
        }

        repository.save(status);

        log.info("사이트 {} 크롤링 상태가 {}으로 변경되었습니다.", siteCode, enabled ? "활성화" : "비활성화");
        return enabled;
    }

    /**
     * 특정 사이트의 크롤링 활성화 상태 확인
     */
    @Transactional(readOnly = true)
    public boolean isSiteEnabled(String siteCode, String siteName) {
        Optional<CrawlerStatus> statusOpt = repository.findBySiteCode(siteCode);

        if (statusOpt.isPresent()) {
            return statusOpt.get().isEnabled();
        } else {
            CrawlerStatus newStatus = CrawlerStatus.builder()
                    .siteCode(siteCode)
                    .siteName(siteName)
                    .enabled(true)
                    .build();

            repository.save(newStatus);
            return true;
        }
    }

    /**
     * 모든 사이트 상태 조회
     */
    @Transactional(readOnly = true)
    public List<CrawlerStatus> getAllSiteStatuses() {
        return repository.findAll();
    }
}