package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

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
    protected void accessLogin() {
        WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mberId")));
        idInput.clear();
        idInput.sendKeys(valueSettings.getBusanMainUsername());

        WebElement pwInput = driver.findElement(By.id("mberPassword"));
        pwInput.clear();
        pwInput.sendKeys(valueSettings.getBusanMainPassword());

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.btnLogin[onclick*='login()']")));
        loginButton.click();
    }

    @Override
    protected void handlePopup() throws InterruptedException {
        if (isElementPresent(By.cssSelector(".jconfirm-box"))) {
            WebElement confirmButton = driver.findElement(By.cssSelector(".jconfirm-buttons .btn.btn-blue"));
            confirmButton.click();

            Thread.sleep(1000);
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
}
