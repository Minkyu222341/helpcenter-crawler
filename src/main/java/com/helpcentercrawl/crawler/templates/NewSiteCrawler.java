//package com.helpcentercrawl.crawler.templates;
//
//import com.helpcentercrawl.crawler.core.AbstractCrawler;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.springframework.stereotype.Component;
//
///**
// * 새로운 사이트 크롤러 - 템플릿
// * 이 템플릿을 복사하여 새로운 사이트 크롤러를 구현할 수 있습니다.
// */
//@Component
//@Slf4j
//public class NewSiteCrawler extends AbstractCrawler {
//
//    // 사이트 관련 상수 정의
//    private static final String LOGIN_URL = "https://example.com/login";
//    private static final String TARGET_URL = "https://example.com/helpdesk/list";
//    private static final String USERNAME = "your-username";
//    private static final String PASSWORD = "your-password";
//
//    // 선택자 정의
//    private static final String TABLE_SELECTOR = "table tbody tr";
//    private static final String PAGINATION_SELECTOR = ".pagination li a";
//
//    // 페이지네이션 사용 여부 및 최대 페이지 수
//    private static final boolean USE_PAGINATION = true;
//    private static final int MAX_PAGES_TO_CHECK = 5;
//
//    @Override
//    public String getSiteName() {
//        return "새로운 사이트 헬프센터";
//    }
//
//    @Override
//    protected void login() {
//        driver.get(LOGIN_URL);
//
//        try {
//            // ID/PW 입력 방식 로그인의 경우
//            WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
//            idInput.sendKeys(USERNAME);
//
//            WebElement pwInput = driver.findElement(By.id("password"));
//            pwInput.sendKeys(PASSWORD);
//
//            // 로그인 버튼 클릭
//            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));
//            loginButton.click();
//
//            // 로그인 대기
//            Thread.sleep(2000);
//        } catch (Exception e) {
//            log.error("로그인 처리 중 오류 발생: {}", e.getMessage());
//        }
//    }
//
//    @Override
//    protected void navigateToTargetPage() {
//        driver.get(TARGET_URL);
//        log.info("{} 대상 페이지 접속 완료", getSiteName());
//
//        // 페이지 로딩 대기 - 테이블이 로드될 때까지
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(TABLE_SELECTOR)));
//        waitForPageLoad();
//    }
//
//    @Override
//    protected void processPageData() throws InterruptedException {
//        if (USE_PAGINATION) {
//            // 여러 페이지 처리
//            processMultiplePages(TABLE_SELECTOR, PAGINATION_SELECTOR, MAX_PAGES_TO_CHECK);
//        } else {
//            // 단일 페이지 처리
//            processSinglePage(TABLE_SELECTOR);
//        }
//    }
//
//    // TODO:필요한 경우에만 isCompleted, extractDateText 메서드 오버라이드
//    /*
//    @Override
//    protected boolean isCompleted(WebElement row) {
//        // 사이트별 완료 상태 확인 로직이 다른 경우 구현
//        return super.isCompleted(row);
//    }
//
//    @Override
//    protected String extractDateText(WebElement row) {
//        // 사이트별 날짜 추출 로직이 다른 경우 구현
//        return super.extractDateText(row);
//    }
//    */
//}