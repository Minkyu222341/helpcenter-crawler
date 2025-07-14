package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : JeonseCrawler
 * author         : MinKyu Park
 * date           : 25. 7. 14.
 * description    : 전세임대포털 내부 프로젝트 지원요청 센터 크롤러
 *                  기존 ID/PW 로그인 방식과 달리 드롭다운 선택 기반 로그인 사용
 *                  로그인 후 전세임대포털 프로젝트 필터링하여 해당 데이터만 크롤링
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 7. 14.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
public class JeonseCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "#tbody tr";
    private static final int TITLE_INDEX = 4;
    private static final int DATE_INDEX = 7;

    public JeonseCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getJeonseLoginUrl());
    }

    @Override
    protected void login() {
        try {
            accessUrl();

            log.info("{} - 로그인 페이지 접속 완료", getSiteName());

            // AJAX로 회원 목록이 로딩될 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("memberId")));

            log.info("{} - 회원 목록 로딩 완료", getSiteName());

            // 드롭다운에서 지정된 회원 선택
            Select memberDropdown = new Select(driver.findElement(By.id("memberId")));
            memberDropdown.selectByValue(valueSettings.getJeonseUsername());

            log.info("{} - 회원 선택 완료: {}", getSiteName(), valueSettings.getJeonseUsername());

            // 로그인 폼 제출
            WebElement submitButton = driver.findElement(By.cssSelector("button.submit"));
            submitButton.click();

            // 로그인 성공 확인 (boardList 페이지로 리다이렉트)
            wait.until(ExpectedConditions.urlContains("boardList"));

            log.info("{} - 로그인 성공, boardList 페이지 이동 완료", getSiteName());

        } catch (Exception e) {
            log.error("{} - 로그인 실패: {}", getSiteName(), e.getMessage());
            throw new RuntimeException("로그인 실패", e);
        }
    }

    @Override
    protected void navigateToTargetPage() throws InterruptedException {
        try {
            log.info("{} - 전세임대포털 프로젝트 필터링 시작", getSiteName());

            // 페이지 초기 로딩 대기 (게시판 목록과 프로젝트 버튼이 AJAX로 로드됨)
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("projectButtons")));

            // 프로젝트 버튼들이 동적으로 생성될 때까지 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(), '전세임대포털')]")));

            log.info("{} - 프로젝트 버튼 로딩 완료", getSiteName());

            // "전세임대포털" 버튼 찾아서 클릭
            WebElement jeonseButton = driver.findElement(
                    By.xpath("//button[contains(text(), '전세임대포털')]"));
            jeonseButton.click();

            log.info("{} - 전세임대포털 버튼 클릭 완료", getSiteName());

            // 필터링된 데이터가 AJAX로 로드될 때까지 대기
            Thread.sleep(3000);

            // 테이블 데이터가 업데이트되었는지 확인
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("#tbody tr")));

            log.info("{} - 전세임대포털 데이터 필터링 완료", getSiteName());

        } catch (Exception e) {
            log.error("{} - 대상 페이지 이동 실패: {}", getSiteName(), e.getMessage());
            throw new RuntimeException("대상 페이지 이동 실패", e);
        }
    }

    @Override
    protected LoginModel getLoginModel() {
        // 이 사이트는 드롭다운 선택 방식으로 LoginModel을 사용하지 않음
        return null;
    }

    @Override
    protected void handlePopup() throws InterruptedException {
        // 이 사이트는 특별한 팝업 처리가 필요 없음
        log.debug("{} - 팝업 처리 불필요", getSiteName());
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
        return valueSettings.getJeonseName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getJeonseCode();
    }
}