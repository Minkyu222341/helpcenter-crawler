package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class GneCrawler extends AbstractCrawler {


    public GneCrawler(CrawlerValueSettings valueSettings) {
        super(valueSettings);
    }

    @Override
    public String getSiteName() {
        return valueSettings.getGneName();
    }

    @Override
    protected void login() {
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

        // 로그인 처리
        try {
            // 로그인 페이지 로드 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idpw-id")));

            // 아이디 및 비밀번호 입력
            WebElement idInput = driver.findElement(By.id("idpw-id"));
            idInput.clear();
            idInput.sendKeys(valueSettings.getGneUsername());

            WebElement pwInput = driver.findElement(By.id("idpw-pw"));
            pwInput.clear();
            pwInput.sendKeys(valueSettings.getGnePassword());

            // 로그인 버튼 클릭
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("idpw-login-btn")));
            loginButton.click();

            // 중복 로그인 알림 처리
            try {
                WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(3));
                Alert alert = alertWait.until(ExpectedConditions.alertIsPresent());

                if (alert != null) {
                    String alertText = alert.getText();

                    // 중복 로그인 관련 알림일 경우 확인 버튼 클릭
                    if (alertText.contains("해당 아이디로 로그인한 이용자가 있습니다")) {
                        alert.accept();

                        // 잠시 대기
                        Thread.sleep(1000);
                    }
                }
            } catch (Exception e) {
                log.error("알림이 없거나 처리 중 오류 발생: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: {}", e.getMessage());
        }
    }

    @Override
    protected void navigateToTargetPage() {
        // 유지관리요청 페이지로 직접 이동
        driver.get(valueSettings.getGneTargetUrl());

        // 페이지 로딩 대기
        waitForPageLoad();
    }

    @Override
    protected void processPageData() {
        // 여러 페이지 처리 공통 메서드 호출 (최대 5페이지)
        processMultiplePages("table tbody tr");
    }
}