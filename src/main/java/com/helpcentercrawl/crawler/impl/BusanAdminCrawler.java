package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
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
    public BusanAdminCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getBusanAdminLoginUrl());
    }

    @Override
    protected void accessLogin() {
        WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mberId")));
        idInput.clear();
        idInput.sendKeys(valueSettings.getBusanAdminUsername());

        WebElement pwInput = driver.findElement(By.id("mberPassword"));
        pwInput.clear();
        pwInput.sendKeys(valueSettings.getBusanAdminPassword());

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn[onclick*='login()']")));
        loginButton.click();
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
        driver.get(valueSettings.getBusanAdminTargetUrl());

        log.info("부산행정 헬프센터 접속 완료");
    }

    @Override
    protected void processPageData() {
        processMultiplePages("table tbody tr");
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
