package com.helpcentercrawl.status.config;

import com.helpcentercrawl.status.entity.CrawlerStatus;
import com.helpcentercrawl.status.repository.CrawlerStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlerStatusManagerTest {

    @Mock
    private CrawlerStatusRepository repository;

    @InjectMocks
    private CrawlerStatusManager crawlerStatusManager;

    private final String TEST_SITE_CODE = "TEST_SITE";
    private final String TEST_SITE_NAME = "테스트 사이트";

    @BeforeEach
    void setUp() {
        reset(repository);
    }

    @Test
    @DisplayName("크롤러 상태 설정 - 기존 상태가 있는 경우")
    void setSiteEnabled_WhenStatusExists() {
        // given
        CrawlerStatus existingStatus = CrawlerStatus.builder()
                .id(1L)
                .siteCode(TEST_SITE_CODE)
                .siteName(TEST_SITE_NAME)
                .enabled(false)
                .build();

        when(repository.findBySiteCode(TEST_SITE_CODE)).thenReturn(Optional.of(existingStatus));
        when(repository.save(any(CrawlerStatus.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        boolean result = crawlerStatusManager.setSiteEnabled(TEST_SITE_CODE, TEST_SITE_NAME, true);

        // then
        assertThat(result).isTrue();
        verify(repository).findBySiteCode(TEST_SITE_CODE);
        verify(repository).save(any(CrawlerStatus.class));
    }

    @Test
    @DisplayName("크롤러 상태 설정 - 기존 상태가 없는 경우")
    void setSiteEnabled_WhenStatusDoesNotExist() {
        // given
        when(repository.findBySiteCode(TEST_SITE_CODE)).thenReturn(Optional.empty());
        when(repository.save(any(CrawlerStatus.class))).thenAnswer(invocation -> {
            CrawlerStatus status = invocation.getArgument(0);
            return CrawlerStatus.builder()
                    .id(1L)
                    .siteCode(status.getSiteCode())
                    .siteName(status.getSiteName())
                    .enabled(status.isEnabled())
                    .build();
        });

        // when
        boolean result = crawlerStatusManager.setSiteEnabled(TEST_SITE_CODE, TEST_SITE_NAME, false);

        // then
        assertThat(result).isFalse();
        verify(repository).findBySiteCode(TEST_SITE_CODE);
        verify(repository).save(any(CrawlerStatus.class));
    }

    @Test
    @DisplayName("모든 사이트 상태 조회")
    void getAllSiteStatuses() {
        // given
        List<CrawlerStatus> statusList = Arrays.asList(
                CrawlerStatus.builder().id(1L).siteCode("SITE1").siteName("사이트1").enabled(true).build(),
                CrawlerStatus.builder().id(2L).siteCode("SITE2").siteName("사이트2").enabled(false).build()
        );

        when(repository.findAll()).thenReturn(statusList);

        // when
        List<CrawlerStatus> result = crawlerStatusManager.getAllSiteStatuses();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSiteCode()).isEqualTo("SITE1");
        assertThat(result.get(0).isEnabled()).isTrue();
        assertThat(result.get(1).getSiteCode()).isEqualTo("SITE2");
        assertThat(result.get(1).isEnabled()).isFalse();
        verify(repository).findAll();
    }
}