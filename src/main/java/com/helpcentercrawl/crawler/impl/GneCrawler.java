package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class GneCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "table tbody tr";
    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 5;

    public GneCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    public String getSiteName() {
        return valueSettings.getGneName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getGneCode();
    }

    @Override
    public Integer getSequence() {
        return 2;
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getGneLoginUrl());

        try {
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/sso/business.jsp' and @title='통합로그인']")));
            loginLink.click();
        } catch (Exception e) {
            log.error("통합로그인 버튼 찾기 실패");
        }
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("idpw-id")
                .pwFieldId("idpw-pw")
                .loginButtonSelector("#idpw-login-btn")
                .username(valueSettings.getGneUsername())
                .password(valueSettings.getGnePassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
    }

    @Override
    protected void handlePopup() throws InterruptedException {
        try {
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();

            if (alertText.contains("로그인")) {
                alert.accept();
                wait.until(d -> {
                    try {
                        d.getTitle();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
            }
        } catch (TimeoutException ignored) {
        }
    }

    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getGneTargetUrl()+PAGE_COUNT);
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
}