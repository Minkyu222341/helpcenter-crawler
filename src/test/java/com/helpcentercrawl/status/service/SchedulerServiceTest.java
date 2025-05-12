package com.helpcentercrawl.status.service;

import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.status.config.CrawlerStatusManager;
import com.helpcentercrawl.status.config.SchedulerStatusManager;
import com.helpcentercrawl.status.dto.SchedulerStatusRequest;
import com.helpcentercrawl.status.dto.SchedulerStatusResponse;
import com.helpcentercrawl.status.dto.SiteStatusRequest;
import com.helpcentercrawl.status.dto.SiteStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock
    private SchedulerStatusManager schedulerStatusManager;

    @Mock
    private CrawlerStatusManager crawlerStatusManager;

    @Mock
    private SiteCrawler crawler1;

    @Mock
    private SiteCrawler crawler2;

    @InjectMocks
    private SchedulerService schedulerService;

    private final String SITE1_CODE = "SITE1";
    private final String SITE1_NAME = "사이트1";
    private final String SITE2_CODE = "SITE2";
    private final String SITE2_NAME = "사이트2";
    private final String UNKNOWN_SITE_CODE = "UNKNOWN";
    private List<SiteCrawler> crawlers;

    @BeforeEach
    void setUp() {
        crawlers = Arrays.asList(crawler1, crawler2);
        ReflectionTestUtils.setField(schedulerService, "crawlers", crawlers);
    }

    @Test
    @DisplayName("스케줄러 상태 조회")
    void getSchedulerStatus() {
        // given
        when(schedulerStatusManager.isSchedulingEnabled()).thenReturn(true);

        // when
        SchedulerStatusResponse response = schedulerService.getSchedulerStatus();

        // then
        assertThat(response.isStatus()).isTrue();
        verify(schedulerStatusManager).isSchedulingEnabled();
    }

    @Test
    @DisplayName("스케줄러 상태 제어")
    void controlSchedulerStatus() {
        // given
        SchedulerStatusRequest request = new SchedulerStatusRequest();
        ReflectionTestUtils.setField(request, "status", false);
        when(schedulerStatusManager.setSchedulingEnabled(false)).thenReturn(false);

        // when
        SchedulerStatusResponse response = schedulerService.controlSchedulerStatus(request);

        // then
        assertThat(response.isStatus()).isFalse();
        verify(schedulerStatusManager).setSchedulingEnabled(false);
    }

    @Test
    @DisplayName("스케줄러 상태 제어 - 상태값 누락")
    void controlSchedulerStatus_WithNullStatus() {
        // given
        SchedulerStatusRequest request = new SchedulerStatusRequest();
        // status를 설정하지 않음

        // when, then
        assertThatThrownBy(() -> schedulerService.controlSchedulerStatus(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상태 값은 필수입니다");
        verify(schedulerStatusManager, never()).setSchedulingEnabled(anyBoolean());
    }

    @Test
    @DisplayName("사이트 상태 조회")
    void getSiteStatus() {
        // given
        when(crawler1.getSiteCode()).thenReturn(SITE1_CODE);
        when(crawler1.getSiteName()).thenReturn(SITE1_NAME);
        when(crawlerStatusManager.isSiteEnabled(SITE1_CODE)).thenReturn(true);

        // when
        SiteStatusResponse response = schedulerService.getSiteStatus(SITE1_CODE);

        // then
        assertThat(response.getSiteCode()).isEqualTo(SITE1_CODE);
        assertThat(response.getSiteName()).isEqualTo(SITE1_NAME);
        assertThat(response.isEnabled()).isTrue();
        verify(crawlerStatusManager).isSiteEnabled(SITE1_CODE);
    }

    @Test
    @DisplayName("사이트 상태 조회 - 존재하지 않는 사이트")
    void getSiteStatus_WithUnknownSiteCode() {
        // given
        // crawler 스텁 설정 (이 테스트에서는 위에서 설정한 SITE1_CODE와 SITE2_CODE만 설정)
        when(crawler1.getSiteCode()).thenReturn(SITE1_CODE);
        when(crawler2.getSiteCode()).thenReturn(SITE2_CODE);

        // when, then
        assertThatThrownBy(() -> schedulerService.getSiteStatus(UNKNOWN_SITE_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사이트 코드입니다: " + UNKNOWN_SITE_CODE);
        verify(crawlerStatusManager, never()).isSiteEnabled(anyString());
    }

    @Test
    @DisplayName("모든 사이트 상태 조회")
    void getAllSitesStatus() {
        // given
        when(crawler1.getSiteCode()).thenReturn(SITE1_CODE);
        when(crawler1.getSiteName()).thenReturn(SITE1_NAME);
        when(crawler2.getSiteCode()).thenReturn(SITE2_CODE);
        when(crawler2.getSiteName()).thenReturn(SITE2_NAME);

        when(crawlerStatusManager.isSiteEnabled(SITE1_CODE)).thenReturn(true);
        when(crawlerStatusManager.isSiteEnabled(SITE2_CODE)).thenReturn(false);

        // when
        List<SiteStatusResponse> responses = schedulerService.getAllSitesStatus();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getSiteCode()).isEqualTo(SITE1_CODE);
        assertThat(responses.get(0).isEnabled()).isTrue();
        assertThat(responses.get(1).getSiteCode()).isEqualTo(SITE2_CODE);
        assertThat(responses.get(1).isEnabled()).isFalse();
        verify(crawlerStatusManager).isSiteEnabled(SITE1_CODE);
        verify(crawlerStatusManager).isSiteEnabled(SITE2_CODE);
    }

    @Test
    @DisplayName("사이트 상태 제어")
    void controlSiteStatus() {
        // given
        when(crawler1.getSiteCode()).thenReturn(SITE1_CODE);
        when(crawler1.getSiteName()).thenReturn(SITE1_NAME);

        SiteStatusRequest request = new SiteStatusRequest();
        ReflectionTestUtils.setField(request, "enabled", false);
        when(crawlerStatusManager.setSiteEnabled(SITE1_CODE, SITE1_NAME, false)).thenReturn(false);

        // when
        SiteStatusResponse response = schedulerService.controlSiteStatus(SITE1_CODE, request);

        // then
        assertThat(response.getSiteCode()).isEqualTo(SITE1_CODE);
        assertThat(response.getSiteName()).isEqualTo(SITE1_NAME);
        assertThat(response.isEnabled()).isFalse();
        verify(crawlerStatusManager).setSiteEnabled(SITE1_CODE, SITE1_NAME, false);
    }

    @Test
    @DisplayName("사이트 상태 제어 - 상태값 누락")
    void controlSiteStatus_WithNullEnabled() {
        // given
        // 이 테스트 케이스에서는 스텁이 필요 없음 - 요청의 validation 로직만 테스트하기 때문
        // 스텁을 제거하고 필요한 설정만 유지
        SiteStatusRequest request = new SiteStatusRequest();
        // enabled를 설정하지 않음

        // when, then
        assertThatThrownBy(() -> schedulerService.controlSiteStatus(SITE1_CODE, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("활성화 상태 값은 필수입니다");
        verify(crawlerStatusManager, never()).setSiteEnabled(anyString(), anyString(), anyBoolean());
    }

    @Test
    @DisplayName("사이트 상태 제어 - 존재하지 않는 사이트")
    void controlSiteStatus_WithUnknownSiteCode() {
        // given
        when(crawler1.getSiteCode()).thenReturn(SITE1_CODE);
        when(crawler2.getSiteCode()).thenReturn(SITE2_CODE);

        SiteStatusRequest request = new SiteStatusRequest();
        ReflectionTestUtils.setField(request, "enabled", true);

        // when, then
        assertThatThrownBy(() -> schedulerService.controlSiteStatus(UNKNOWN_SITE_CODE, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사이트 코드입니다: " + UNKNOWN_SITE_CODE);
        verify(crawlerStatusManager, never()).setSiteEnabled(anyString(), anyString(), anyBoolean());
    }

}