package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : GnuCrawler
 * author         : MinKyu Park
 * date           : 25. 5. 2.
 * description    : 경상국립대 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 2.        MinKyu Park       최초 생성
 * 25. 5. 14.       MinKyu Park       다중 URL 크롤링 기능 추가
 */
@Slf4j
@Component
public class GnuCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "table > tbody > tr";
    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 4;

    public GnuCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getGnuLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("a.btn_login")
                .username(valueSettings.getGnuUsername())
                .password(valueSettings.getGnuPassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
    }

    @Override
    protected void handlePopup() {
        try {
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            alertWait.until(ExpectedConditions.alertIsPresent());

            driver.switchTo().alert().accept();
        } catch (TimeoutException e) {
            log.info("Alert 창이 나타나지 않았습니다.");
        }
    }

    @Override
    protected void navigateToTargetPage() {
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getGnuMainTargetUrl())
                .queryParam(QUERY_PARAM_NAME, PARAM_PAGE_COUNT)
                .build()
                .toUriString();

        driver.get(targetUrl);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TABLE_SELECTOR)));
        } catch (Exception e) {
            log.warn("경상국립대 메인페이지 테이블 로딩 실패: {}", e.getMessage());
        }
    }

    /**
     * 다중 URL 크롤링을 위해 processMultiplePages 메서드 오버라이드
     */
    @Override
    protected void processMultiplePages() {
        log.info("{} - 메인 사이트 크롤링 시작", getSiteName());
        super.processMultiplePages();

        log.info("{} - 서브 사이트 크롤링 시작", getSiteName());
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getGnuSubTargetUrl())
                .queryParam(QUERY_PARAM_NAME, PARAM_PAGE_COUNT)
                .build()
                .toUriString();

        driver.get(targetUrl);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TABLE_SELECTOR)));
        } catch (Exception e) {
            log.warn("경상국립대 서브페이지 테이블 로딩 실패: {}", e.getMessage());
        }
        super.processMultiplePages();
    }

    @Override
    protected String getTableSelector() {
        return TABLE_SELECTOR;
    }

    @Override
    protected int getTitleIndex() {
        return TITLE_INDEX;
    }

    @Override
    protected int getDateIndex() {
        return DATE_INDEX;
    }

    @Override
    public String getSiteName() {
        return valueSettings.getGnuName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getGnuCode();
    }
}