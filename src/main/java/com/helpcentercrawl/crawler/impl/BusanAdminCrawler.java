package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : BusanAdminCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 부산행정 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */

@Slf4j
@Component
public class BusanAdminCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "table tbody tr";

    // TD 인덱스 상수 (0부터 시작)
    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 4;

    public BusanAdminCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("a.btn[onclick*='login()']")
                .username(valueSettings.getBusanAdminUsername())
                .password(valueSettings.getBusanAdminPassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.or(
                        ExpectedConditions.urlContains("main"),
                        ExpectedConditions.urlContains("changePassword")
                ))
                .build();
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getBusanAdminLoginUrl());
    }

    @Override
    protected void handlePopup() throws InterruptedException {
        try {
            WebDriverWait firstAlertWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            firstAlertWait.until(ExpectedConditions.alertIsPresent());

            Alert firstAlert = driver.switchTo().alert();
            String firstAlertText = firstAlert.getText();
            log.info("부산행정 - 첫 번째 Alert: {}", firstAlertText);
            firstAlert.accept();

            try {
                WebDriverWait secondAlertWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                secondAlertWait.until(ExpectedConditions.alertIsPresent());

                Alert secondAlert = driver.switchTo().alert();
                String secondAlertText = secondAlert.getText();
                log.info("부산행정 - 두 번째 Alert: {}", secondAlertText);
                secondAlert.accept();

                log.info("부산행정 - 두 개의 Alert 처리 완료");

            } catch (TimeoutException e) {
                log.debug("부산행정 - 두 번째 Alert이 나타나지 않았습니다.");
            }

        } catch (TimeoutException e) {
            log.debug("부산행정 - 첫 번째 Alert 창이 나타나지 않았습니다.");
        } catch (NoAlertPresentException e) {
            log.debug("부산행정 - Alert이 존재하지 않습니다.");
        }
    }

    @Override
    protected void navigateToTargetPage() {
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getBusanAdminTargetUrl())
                .queryParam(QUERY_PARAM_NAME, PARAM_PAGE_COUNT)
                .build()
                .toUriString();

        driver.get(targetUrl);
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
        return valueSettings.getBusanAdminName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getBusanAdminCode();
    }
}