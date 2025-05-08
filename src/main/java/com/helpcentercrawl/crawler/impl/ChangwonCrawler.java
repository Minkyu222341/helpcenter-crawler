package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : ChangwonCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 창원대 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */

@Slf4j
@Component
public class ChangwonCrawler extends AbstractCrawler {


    public ChangwonCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }


    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getChangwonLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("a.btn_Login")
                .username(valueSettings.getChangwonUsername())
                .password(valueSettings.getChangwonPassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("update"))
                .build();
    }

    @Override
    protected void handlePopup() {
    }


    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getChangwonTargetUrl());
        log.info("창원 헬프센터 접속 완료");
    }

    @Override
    protected void processPageData() throws InterruptedException {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#nttTable > tbody > tr")));

        processMultiplePages("table#nttTable > tbody > tr");
    }

    @Override
    public String getSiteName() {
        return valueSettings.getChangwonName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getChangwonCode();
    }

    @Override
    public Integer getSequence() {
        return 5;
    }
}