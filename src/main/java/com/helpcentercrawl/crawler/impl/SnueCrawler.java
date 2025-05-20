package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : SnueCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 21.
 * description    : 서울교대 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 21.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
public class SnueCrawler extends AbstractCrawler {
    private static final int TITLE_INDEX = 1;
    private static final int DATE_INDEX = 3;
    private static final String TABLE_SELECTOR = "tbody > tr";

    public SnueCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getSnueLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("button.btnLogin")
                .username(valueSettings.getSnueUsername())
                .password(valueSettings.getSnuePassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.or(
                        ExpectedConditions.urlContains("main"),
                        ExpectedConditions.urlContains("update")
                ))
                .build();
    }

    @Override
    protected void handlePopup() {
        if (isElementPresent(By.id("mainBtn"))) {
            WebElement laterButton = driver.findElement(By.id("mainBtn"));
            laterButton.click();
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    @Override
    protected void navigateToTargetPage() throws InterruptedException {
        Thread.sleep(1000);

        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getSnueTargetUrl())
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
        return valueSettings.getSnueName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getSnueCode();
    }

}