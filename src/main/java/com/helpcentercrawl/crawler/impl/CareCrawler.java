package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : CareCrawler
 * author         : MinKyu Park
 * date           : 25. 5. 7.
 * description    : 늘봄학교 서비스 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 7.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
public class CareCrawler extends AbstractCrawler {
    public CareCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getCareLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("button.btnLogin")
                .username(valueSettings.getCareUsername())
                .password(valueSettings.getCarePassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
    }

    @Override
    protected void handlePopup() throws InterruptedException {

    }

    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getCareTargetUrl());
        log.info("늘봄학교 서비스 헬프센터 접속 완료");
    }

    @Override
    protected void processPageData() throws InterruptedException {
        processMultiplePages("table > tbody > tr");
    }

    @Override
    public String getSiteName() {
        return valueSettings.getCareName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getCareCode();
    }

    @Override
    public Integer getSequence() {
        return 9;
    }
}
