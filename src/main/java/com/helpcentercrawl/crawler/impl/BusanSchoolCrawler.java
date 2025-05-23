package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

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
 * 25. 5. 13.        MinKyu Park       페이징 제거 및 POST 방식 처리 추가
 */

@Slf4j
@Component
public class BusanSchoolCrawler extends AbstractCrawler {

    private static final String TABLE_SELECTOR = "div.board-text > table > tbody > tr";
    private static final int TITLE_INDEX = 1;
    private static final int DATE_INDEX = 3;

    // DOM 요소 ID 상수 추가
    private static final String FORM_ID = "srchForm";
    private static final String LIST_COUNT_SELECT_ID = "listCo";
    private static final String FORM_ACTION_URL = "/help/na/ntt/selectNttList.do";

    // JavaScript 스크립트 템플릿 상수 추가
    private static final String JS_SET_LIST_COUNT_TEMPLATE =
            "var form = document.getElementById('%s');" +
                    "var listCoSelect = document.getElementById('%s');" +
                    "if (listCoSelect) {" +
                    "    listCoSelect.disabled = true;" +
                    "}" +
                    "var existingInput = form.querySelector('input[name=\"%s\"]');" +
                    "if (existingInput) {" +
                    "    form.removeChild(existingInput);" +
                    "}" +
                    "var input = document.createElement('input');" +
                    "input.type = 'hidden';" +
                    "input.name = '%s';" +
                    "input.value = '%s';" +
                    "form.appendChild(input);";

    private static final String JS_SUBMIT_FORM_TEMPLATE =
            "document.getElementById('%s').action = '%s'; " +
                    "document.getElementById('%s').submit();";

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

        // 페이지당 표시 건수 설정
        setCustomListCount();
    }

    /**
     * 페이지에 표시할 데이터 수를 JavaScript로 설정하고 폼 제출
     */
    private void setCustomListCount() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            if (PARAM_PAGE_COUNT == null) {
                log.warn("{} - PARAM_PAGE_COUNT가 설정되지 않아 기본값 20으로 설정합니다.", getSiteName());
                PARAM_PAGE_COUNT = "20";
            }

            // 리스트 개수 설정 스크립트 생성
            String setListCountScript = String.format(
                    JS_SET_LIST_COUNT_TEMPLATE,
                    FORM_ID,
                    LIST_COUNT_SELECT_ID,
                    LIST_COUNT_SELECT_ID,
                    LIST_COUNT_SELECT_ID,
                    PARAM_PAGE_COUNT
            );

            js.executeScript(setListCountScript);

            // 폼 제출 스크립트 생성
            String submitFormScript = String.format(
                    JS_SUBMIT_FORM_TEMPLATE,
                    FORM_ID,
                    FORM_ACTION_URL,
                    FORM_ID
            );

            js.executeScript(submitFormScript);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TABLE_SELECTOR)));

        } catch (Exception e) {
            log.error("페이지당 표시 건수 설정 중 오류 발생: {}", e.getMessage(), e);
        }
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

    /**
     * 부산 교육청은 두 개의 URL을 크롤링해야 하므로 processMultiplePages 메서드 오버라이드
     */
    @Override
    protected void processMultiplePages() {
        log.info("{} - 학교 수정요청 크롤링 시작", getSiteName());
        super.processMultiplePages();

        log.info("{} - 유치원 수정요청 크롤링 시작", getSiteName());
        driver.get(valueSettings.getBusanSchoolKindergartenTargetUrl());

        setCustomListCount();

        super.processMultiplePages();
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