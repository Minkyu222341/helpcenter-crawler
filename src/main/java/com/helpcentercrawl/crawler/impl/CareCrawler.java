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

    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 4;
    private static final String TABLE_SELECTOR = "tbody > tr";

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
        driver.get(valueSettings.getCareTargetUrl()+PAGE_COUNT);
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
