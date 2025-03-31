package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangwonCrawler extends AbstractCrawler {


    public ChangwonCrawler(CrawlerValueSettings valueSettings) {
        super(valueSettings);
    }

    @Override
    public String getSiteName() {
        return valueSettings.getChangwonName();
    }

    @Override
    protected void login() throws InterruptedException {
        // 로그인 URL로 직접 접속 (아이디와 비밀번호가 포함된 URL)
        String loginUrl = String.format("%s?mberId=%s&mberPassword=%s",
                valueSettings.getChangwonLoginUrl(),
                valueSettings.getChangwonUsername(),
                valueSettings.getChangwonPassword());
        driver.get(loginUrl);

        // 로그인 처리 대기
        Thread.sleep(2000); // 페이지 로딩 대기
    }

    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getChangwonTargetUrl());
        log.info("창원 헬프센터 접속 완료");

        // 페이지 완전 로딩 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#nttTable")));
        waitForPageLoad();
    }

    @Override
    protected void processPageData() {
        // 단일 페이지 처리 공통 메서드 호출
        processMultiplePages("table#nttTable > tbody > tr");
    }
}