package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

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

    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getSnueTargetUrl());
        log.info("서울교대 헬프센터 접속 완료");
    }

    @Override
    protected void processPageData() {
        processMultiplePages("div.BD_list table tbody tr");
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
