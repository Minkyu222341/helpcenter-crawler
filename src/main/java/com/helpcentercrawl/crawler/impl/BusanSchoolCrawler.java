package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BusanSchoolCrawler extends AbstractCrawler {

    public BusanSchoolCrawler(CrawlerValueSettings valueSettings) {
        super(valueSettings);
    }

    @Override
    public String getSiteName() {
        return valueSettings.getBusanSchoolName();
    }

    @Override
    protected void login() throws InterruptedException {
        // 로그인 페이지로 이동
        driver.get(valueSettings.getBusanSchoolLoginUrl());
        log.info("부산교육청-학교 헬프센터 로그인 페이지 접속 완료");

        try {
            // 로그인 폼 요소들이 로드될 때까지 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mberId")));

            // 아이디 입력
            WebElement idInput = driver.findElement(By.id("mberId"));
            idInput.clear();
            idInput.sendKeys(valueSettings.getBusanSchoolUsername());

            // 비밀번호 입력
            WebElement pwInput = driver.findElement(By.id("mberPassword"));
            pwInput.clear();
            pwInput.sendKeys(valueSettings.getBusanSchoolPassword());

            // 로그인 버튼 클릭
            WebElement loginButton = driver.findElement(By.cssSelector("a.btn_login"));
            loginButton.click();

            // 로그인 처리 대기
            Thread.sleep(3000);

            // 알림창이 뜨는지 확인 (로그인 성공 시 알림창이 표시됨)
            try {
                WebElement confirmButton = driver.findElement(By.cssSelector("button.btn_st.blue, button.확인"));
                if (confirmButton.isDisplayed()) {
                    confirmButton.click();
                }
            } catch (Exception e) {
                log.debug("알림창이 표시되지 않았습니다: {}", e.getMessage());
            }

            log.info("부산교육청-학교 헬프센터 로그인 성공");

        } catch (Exception e) {
            log.error("부산교육청-학교 헬프센터 로그인 중 오류 발생: {}", e.getMessage());
            log.error("오류 상세 정보:", e);
            throw e;
        }
    }

    @Override
    protected void navigateToTargetPage() {
        // 대상 페이지로 이동 (학교 수정요청 게시판)
        driver.get(valueSettings.getBusanSchoolSchoolTargetUrl());
        log.info("부산교육청-학교 수정요청 게시판 접속 완료");

        // 페이지 로딩 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

        // 페이지 완전 로딩 대기
        waitForPageLoad();
    }

    @Override
    protected void processPageData() {
        // 현재 페이지 처리 (학교 수정요청 게시판)
        processMultiplePages("table > tbody > tr");

        // 유치원 수정요청 게시판으로 이동
        driver.get(valueSettings.getBusanSchoolKindergartenTargetUrl());
        log.info("부산교육청-유치원 수정요청 게시판 접속 완료");

        // 페이지 로딩 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table")));

        // 페이지 완전 로딩 대기
        waitForPageLoad();

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