package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

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
 */
@Slf4j
@Component
public class GnuCrawler extends AbstractCrawler {
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
                .username(valueSettings.getCareUsername())
                .password(valueSettings.getCarePassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
    }

    @Override
    protected void handlePopup(){
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
        driver.get(valueSettings.getGnuMainTargetUrl());
        log.info("경상국립대 헬프센터 메인페이지 접속 완료");
    }

    @Override
    protected void processPageData(){
        processMultiplePages("table > tbody > tr");

        driver.get(valueSettings.getGnuSubTargetUrl());
        log.info("경상국립대 헬프센터 서브페이지 접속 완료");

    }

    @Override
    public String getSiteName() {
        return valueSettings.getGnuName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getGnuCode();
    }

    @Override
    public Integer getSequence() {
        return 7;
    }
}
