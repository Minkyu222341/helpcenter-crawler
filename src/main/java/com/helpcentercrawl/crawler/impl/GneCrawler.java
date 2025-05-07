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

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : GneCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 경남행정 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */

@Slf4j
@Component
public class GneCrawler extends AbstractCrawler {


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
    protected void accessUrl() {
        // 로그인 URL 직접 접근
        driver.get(valueSettings.getGneLoginUrl());
        log.info("경남 헬프센터 접속 완료");

        // 통합로그인 버튼 클릭
        try {
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/sso/business.jsp' and @title='통합로그인']")));
            loginLink.click();
        } catch (Exception e) {
            log.error("통합로그인 버튼 찾기 실패, 다음 단계로 넘어갑니다.");
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

            if (alertText.contains("해당 아이디로 로그인한 이용자가 있습니다")) {
                alert.accept();
                // 명시적 대기로 변경
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
        driver.get(valueSettings.getGneTargetUrl());
    }

    @Override
    protected void processPageData() {
        processMultiplePages("table tbody tr");
    }
}