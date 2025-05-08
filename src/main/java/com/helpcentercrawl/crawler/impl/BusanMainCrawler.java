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

        }
    }


    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getBusanMainTargetUrl());
        log.info("부산본청 헬프센터 접속 완료");
    }

    @Override
    protected void processPageData() {
        processMultiplePages("table tbody tr");
    }

    @Override
    public String getSiteName() {
        return valueSettings.getBusanMainName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getBusanMainCode();
    }

    @Override
    public Integer getSequence() {
        return 3;
    }
}
