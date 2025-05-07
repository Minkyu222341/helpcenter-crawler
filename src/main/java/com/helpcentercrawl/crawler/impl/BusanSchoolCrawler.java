package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : BusanSchoolCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 부산학교 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */

@Slf4j
@Component
public class BusanSchoolCrawler extends AbstractCrawler {


    public BusanSchoolCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getBusanSchoolLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        return LoginModel.builder()
                .idFieldId("mberId")
                .pwFieldId("mberPassword")
                .loginButtonSelector("a.btn_login")
                .username(valueSettings.getBusanSchoolUsername())
                .password(valueSettings.getBusanSchoolPassword())
                .jsLogin(false)
                .successCondition(ExpectedConditions.urlContains("main"))
                .build();
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
        driver.get(valueSettings.getBusanSchoolSchoolTargetUrl());
        log.info("부산교육청-학교 수정요청 게시판 접속 완료");
    }

    @Override
    protected void processPageData() {
        processMultiplePages("table > tbody > tr");

        driver.get(valueSettings.getBusanSchoolKindergartenTargetUrl());
        log.info("부산교육청-유치원 수정요청 게시판 접속 완료");

        processMultiplePages("table > tbody > tr");
    }

    @Override
    protected boolean isCompleted(WebElement row) {
        try {
            List<WebElement> images = row.findElements(By.cssSelector("img[src*='btn_comp']"));
            if (!images.isEmpty()) {
                return true;
            }

            String rowText = row.getText();
            return rowText.contains("완료");
        } catch (Exception e) {
            log.debug("완료 상태 확인 오류: {}", e.getMessage());
        }

        return false;
    }

    @Override
    protected String extractDateText(WebElement row) {
        try {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 4) {
                return cells.get(3).getText().trim();
            }
        } catch (Exception e) {
            log.debug("날짜 텍스트 추출 오류: {}", e.getMessage());
        }

        return "";
    }

    @Override
    public String getSiteName() {
        return valueSettings.getBusanSchoolName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getBusanSchoolCode();
    }
}