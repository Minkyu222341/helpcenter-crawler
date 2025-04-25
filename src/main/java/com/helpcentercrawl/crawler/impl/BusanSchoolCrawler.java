package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

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
    public String getSiteName() {
        return valueSettings.getBusanSchoolName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getBusanSchoolCode();
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getBusanSchoolLoginUrl());
    }

    @Override
    protected void accessLogin() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mberId")));

        WebElement idInput = driver.findElement(By.id("mberId"));
        idInput.clear();
        idInput.sendKeys(valueSettings.getBusanSchoolUsername());

        WebElement pwInput = driver.findElement(By.id("mberPassword"));
        pwInput.clear();
        pwInput.sendKeys(valueSettings.getBusanSchoolPassword());

        WebElement loginButton = driver.findElement(By.cssSelector("a.btn_login"));
        loginButton.click();
    }

    @Override
    protected void handlePopup() {
        try {
            WebElement confirmButton = driver.findElement(By.cssSelector("button.btn_st.blue, button.확인"));
            if (confirmButton.isDisplayed()) {
                confirmButton.click();
            }
        } catch (Exception e) {
            log.debug("알림창이 표시되지 않았습니다: {}", e.getMessage());
        }
    }


    @Override
    protected void navigateToTargetPage() {
        // 대상 페이지로 이동 (학교 수정요청 게시판)
        driver.get(valueSettings.getBusanSchoolSchoolTargetUrl());
        log.info("부산교육청-학교 수정요청 게시판 접속 완료");
    }

    @Override
    protected void processPageData() {
        // 현재 페이지 처리 (학교 수정요청 게시판)
        processMultiplePages("table > tbody > tr");

        // 유치원 수정요청 게시판으로 이동
        driver.get(valueSettings.getBusanSchoolKindergartenTargetUrl());
        log.info("부산교육청-유치원 수정요청 게시판 접속 완료");

        // 유치원 수정요청 게시판 처리
        processMultiplePages("table > tbody > tr");
    }

    @Override
    protected boolean isCompleted(WebElement row) {
        try {
            // 완료 상태 확인 (이미지를 통해 확인)
            List<WebElement> images = row.findElements(By.cssSelector("img[src*='btn_comp']"));
            if (!images.isEmpty()) {
                return true;
            }

            // 텍스트로 완료 상태 확인
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
            // 날짜 셀 가져오기 (4번째 td가 날짜)
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 4) {
                return cells.get(3).getText().trim();
            }
        } catch (Exception e) {
            log.debug("날짜 텍스트 추출 오류: {}", e.getMessage());
        }

        return "";
    }
}