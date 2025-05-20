package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : BusanMainCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 부산본청 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
public class BusanMainCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "table tbody tr";
    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 4;

    public BusanMainCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getBusanMainLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("button.btnLogin[onclick*='login()']")
                .username(valueSettings.getBusanMainUsername())
                .password(valueSettings.getBusanMainPassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
    }

    @Override
    protected void handlePopup() {
        try {
            WebDriverWait popupWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            popupWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".jconfirm-box")));

            WebElement confirmButton = driver.findElement(By.cssSelector(".jconfirm-buttons .btn.btn-blue"));
            confirmButton.click();
        } catch (TimeoutException ignored) {
            // 팝업이 없는 경우 무시
        }
    }

    @Override
    protected void navigateToTargetPage() {
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getBusanMainTargetUrl())
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
        return valueSettings.getBusanMainName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getBusanMainCode();
    }

}