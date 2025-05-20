package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class ChangwonCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "table#nttTable > tbody > tr";
    private static final int TITLE_INDEX = 1;
    private static final int DATE_INDEX = 3;

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
        // 팝업 처리 없음
    }

    @Override
    protected void navigateToTargetPage() {
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getChangwonTargetUrl())
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
        return valueSettings.getChangwonName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getChangwonCode();
    }

}