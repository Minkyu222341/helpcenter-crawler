package com.helpcentercrawl.crawler.core;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.dto.CrawlResultDto;
import com.helpcentercrawl.crawler.dto.CrawlSaveDto;
import com.helpcentercrawl.crawler.interfaces.SiteCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * packageName    : com.helpcentercrawl.crawler.core
 * fileName       : AbstractCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 크롤러 구현체들의 공통 동작을 정의한 추상 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCrawler implements SiteCrawler {

    protected final CrawlResultService crawlResultService;
    protected final CrawlerValueSettings valueSettings;
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected LocalDate today;
    protected AtomicInteger todayTotal = new AtomicInteger(0);
    protected AtomicInteger todayCompleted = new AtomicInteger(0);
    protected AtomicInteger todayNotCompleted = new AtomicInteger(0);

    private static final String LOCAL_CHROME_DRIVER_PATH = "src/main/resources/chromedriver/windows/chromedriver.exe";
    private static final String PROC_CHROME_DRIVER_PATH = "/usr/bin/chromedriver";
    private static final String SET_PROPERTY = "webdriver.chrome.driver";
    private static final String PAGINATION_SELECTOR = ".pagination li a";

    /**
     * 템플릿 메서드 - 크롤링 전체 프로세스를 정의합니다.
     */
    @Override
    public final void crawl() {
        todayTotal.set(0);
        todayCompleted.set(0);
        todayNotCompleted.set(0);

        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            System.setProperty(SET_PROPERTY, LOCAL_CHROME_DRIVER_PATH);
        } else {
            System.setProperty(SET_PROPERTY, PROC_CHROME_DRIVER_PATH);
        }

        try {
            // 1. 웹드라이버 초기화
            initializeWebDriver();

            // 2. 로그인 처리
            login();

            // 3. 대상 페이지로 이동
            navigateToTargetPage();

            // 4. 페이지에서 데이터 추출 및 처리
            processPageData();

            // 5. Redis에 크롤링 결과 저장
            saveCrawlResultToRedis();

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage());
            log.error("오류 상세 정보:", e);
        } finally {
            cleanup();
        }
    }

    /**
     * 웹드라이버 초기화 메서드
     */
    protected void initializeWebDriver() {
        ChromeOptions options = getChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.images", 2);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        today = LocalDate.now();
        log.info("{} 웹드라이버 초기화 완료", getSiteName());
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Docker에서 실행 시 필요
        options.addArguments("--no-sandbox"); // Docker에서 필요
        options.addArguments("--disable-dev-shm-usage"); // Docker에서 필요
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--blink-settings=imagesEnabled=false");

        // 페이지 로드 전략 설정
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

    /**
     * 로그인 처리 메서드
     */
    protected void login() {
        try {
            accessUrl();

            LoginModel loginModel = getLoginModel();

            if (loginModel.isJsLogin()) {
                jsLogin(loginModel);
            } else {
                standardLogin(loginModel);
            }

            log.info("{} 로그인 성공", getSiteName());
        } catch (Exception e) {
            log.error("{} 로그인 처리 중 오류 발생: {}", getSiteName(), e.getMessage(), e);
            throw new RuntimeException("로그인 실패", e);
        }
    }
    /**
     * 표준 로그인 수행 메서드
     */
    private void standardLogin(LoginModel loginModel) throws InterruptedException {
        WebElement idInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(loginModel.getIdFieldId())));
        idInput.clear();
        idInput.sendKeys(loginModel.getUsername());

        WebElement pwInput = driver.findElement(By.id(loginModel.getPwFieldId()));
        pwInput.clear();
        pwInput.sendKeys(loginModel.getPassword());

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(loginModel.getLoginButtonSelector())));
        loginButton.click();

        handlePopup();

        wait.until(loginModel.getSuccessCondition());
    }

    /**
     * JavaScript 기반 로그인 수행 메서드 (해양대학교)
     */
    private void jsLogin(LoginModel loginModel) throws InterruptedException {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(loginModel.getIdFieldId())));

        WebElement idInput = driver.findElement(By.id(loginModel.getIdFieldId()));
        idInput.clear();
        idInput.sendKeys(loginModel.getUsername());

        WebElement pwInput = driver.findElement(By.id(loginModel.getPwFieldId()));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", pwInput, loginModel.getPassword());

        js.executeScript(loginModel.getJsLoginScript());

        handlePopup();

        // 로그인 성공 조건 대기
        wait.until(loginModel.getSuccessCondition());
    }

    /**
     * 로그인 설정 가져오기 (각 크롤러에서 구현)
     */
    protected abstract LoginModel getLoginModel();

    /**
     * 로그인 URL 접근 메서드
     */
    protected abstract void accessUrl();

    /**
     * 팝업 처리 메서드
     */
    protected abstract void handlePopup() throws InterruptedException;


    /**
     * 대상 페이지로 이동하는 메서드
     */
    protected abstract void navigateToTargetPage();

    /**
     * 페이지 데이터 처리 메서드
     */
    protected abstract void processPageData() throws InterruptedException;

    /**
     * 여러 페이지의 데이터를 처리하는 공통 메서드
     *
     * @param tableSelector 테이블 행 선택자
     */
    protected void processMultiplePages(String tableSelector) {
        int maxPages = getMaxPagesCount();

        // 각 페이지 탐색 현재 3페이지까지만 탐색
        for (int currentPage = 1; currentPage <= Math.min(maxPages, 3); currentPage++) {
            if (currentPage > 1) {
                navigateToPage(currentPage);
            }

            // 현재 페이지의 테이블 행 가져오기
            List<WebElement> rows = driver.findElements(By.cssSelector(tableSelector));

            // 행 처리 공통 메서드 호출
            processRows(rows);
        }
    }

    /**
     * 페이지 수 확인 공통 메서드
     */
    protected int getMaxPagesCount() {
        int maxPages = 1;
        try {
            // 페이지네이션 요소 찾기
            List<WebElement> pagination = driver.findElements(By.cssSelector(PAGINATION_SELECTOR));

            // 페이지 수 파악 (마지막 페이지 번호 찾기)
            for (WebElement pageLink : pagination) {
                String pageText = pageLink.getText().trim();
                if (pageText.matches("\\d+")) {
                    int pageNum = Integer.parseInt(pageText);
                    if (pageNum > maxPages) {
                        maxPages = pageNum;
                    }
                }
            }
        } catch (Exception e) {
            log.error("페이지네이션 요소를 찾을 수 없습니다: {}", e.getMessage());
        }
        return maxPages;
    }

    /**
     * 페이지를 이동 하는 메서드
     */
    protected void navigateToPage(int pageNumber) {
        try {
            String pageXpath = "//a[text()='" + pageNumber + "']";
            WebElement pageLink = driver.findElement(By.xpath(pageXpath));
            pageLink.click();

        } catch (Exception e) {
            log.error("{}페이지로 이동 중 오류 발생: {}", pageNumber, e.getMessage());
            throw e;
        }
    }

    /**
     * 테이블 행을 처리하는 공통 메서드
     *
     * @param rows 처리할 웹 요소(행) 목록
     */
    protected void processRows(List<WebElement> rows) {
        for (WebElement row : rows) {
            try {
                String dateText = extractDateText(row);
                if (dateText.isEmpty()) {
                    continue;
                }

                if (isToday(dateText)) {
                    todayTotal.incrementAndGet(); // 총 개수 증가

                    if (isCompleted(row)) {
                        todayCompleted.incrementAndGet();
                    } else {
                        todayNotCompleted.incrementAndGet();
                    }
                }
            } catch (Exception e) {
                log.error("행 처리 중 오류 발생: {}", e.getMessage());
            }
        }
    }

    /**
     * 날짜 텍스트 추출 유틸리티 메서드
     */
    protected String extractDateText(WebElement row) {
        try {
            List<WebElement> cells = row.findElements(By.tagName("td"));

            // 모든 셀에서 날짜 형식 찾기
            for (WebElement cell : cells) {
                String cellText = cell.getText().trim();
                // 날짜 형식 패턴 확인 (YYYY. MM. DD 또는 YYYY.MM.DD)
                if (cellText.matches("\\d{4}[.\\s]+\\d{1,2}[.\\s]+\\d{1,2}.*")) {
                    return cellText;
                }
            }
        } catch (Exception e) {
            log.debug("날짜 텍스트 추출 오류: {}", e.getMessage());
        }

        return "";
    }

    /**
     * 오늘 날짜인지 확인하는 유틸리티 메서드
     */
    protected boolean isToday(String dateText) {
        try {
            // 1. 직접 문자열 포함 여부 확인
            String todayString = today.getYear() + ". " + today.getMonthValue() + ". " + today.getDayOfMonth();
            String todayStringWithZero = today.getYear() + ". " +
                    (today.getMonthValue() < 10 ? "0" + today.getMonthValue() : today.getMonthValue()) + ". " +
                    (today.getDayOfMonth() < 10 ? "0" + today.getDayOfMonth() : today.getDayOfMonth());

            if (dateText.contains(todayString) || dateText.contains(todayStringWithZero)) {
                return true;
            }

            // 2. 정규화된 비교
            String datePart = dateText.split("\\s+")[0];  // 공백으로 분리하여 첫 부분만 사용
            String normalizedDate = datePart.replaceAll("[^0-9]", "");

            if (normalizedDate.length() >= 8) {
                int year = Integer.parseInt(normalizedDate.substring(0, 4));
                int month = Integer.parseInt(normalizedDate.substring(4, 6));
                int day = Integer.parseInt(normalizedDate.substring(6, 8));

                if (year == today.getYear() && month == today.getMonthValue() && day == today.getDayOfMonth()) {
                    return true;
                }
            }

            // 3. 날짜 파싱 (마침표로 구분된 경우)
            String[] dateParts = dateText.split("[.\\s]+");
            if (dateParts.length >= 3) {
                int year = Integer.parseInt(dateParts[0].trim());
                int month = Integer.parseInt(dateParts[1].trim());
                int day = Integer.parseInt(dateParts[2].trim());

                if (year == today.getYear() && month == today.getMonthValue() && day == today.getDayOfMonth()) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.debug("날짜 비교 오류: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 완료 상태인지 확인하는 유틸리티 메서드
     */
    protected boolean isCompleted(WebElement row) {
        try {
            // 1. 완료 관련 span 태그 텍스트 확인
            List<WebElement> spans = row.findElements(By.cssSelector("span"));
            for (WebElement span : spans) {
                String spanText = span.getText().trim();
                String style = span.getAttribute("style");
                if (spanText.equals("완료") || spanText.equals("답변완료") ||
                        (style != null && style.contains("#567cb8"))) {
                    return true;
                }
            }

            // 2. 완료 관련 이미지 확인
            List<WebElement> imgs = row.findElements(By.tagName("img"));
            for (WebElement img : imgs) {
                String alt = img.getAttribute("alt");
                String src = img.getAttribute("src");
                if ((alt != null && (alt.equals("완료") || alt.equals("답변완료"))) ||
                        (src != null && (src.contains("btn_comp") || src.contains("compt")))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.debug("완료 상태 확인 오류: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Redis에 크롤링 결과 저장 메서드
     */
    protected void saveCrawlResultToRedis() {
        LocalDateTime lastUpdatedTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        try {
            CrawlSaveDto currentResult = CrawlSaveDto.builder()
                    .siteCode(getSiteCode())
                    .siteName(getSiteName())
                    .completedCount(todayCompleted.get())
                    .notCompletedCount(todayNotCompleted.get())
                    .totalCount(todayTotal.get())
                    .crawlDate(today)
                    .sequence(getSequence())
                    .lastUpdatedAt(lastUpdatedTime)
                    .build();

            CrawlResultDto previousResult = crawlResultService.getCrawlResult(getSiteCode(), today);

            if (isDuplicateData(previousResult, currentResult)) {
                log.info("중복된 크롤링 결과로 Redis에 저장하지 않음: {}", getSiteName());
                return;
            }

            crawlResultService.saveCrawlResult(currentResult);
            log.info("Redis에 크롤링 결과 저장 완료: {}", getSiteName());
        } catch (Exception e) {
            log.error("Redis에 크롤링 결과 저장 중 오류 발생: {}", e.getMessage());
            log.error("오류 상세 정보:", e);
        }
    }

    /**
     * 크롤링 중복 데이터 확인 메서드
     */
    private boolean isDuplicateData(CrawlResultDto previousResult, CrawlSaveDto currentResult) {
        if (previousResult != null) {
            return Objects.equals(previousResult.getTotalCount(), currentResult.getTotalCount()) &&
                    Objects.equals(previousResult.getCompletedCount(), currentResult.getCompletedCount()) &&
                    Objects.equals(previousResult.getNotCompletedCount(), currentResult.getNotCompletedCount());

        }
        return false;
    }

    /**
     * 리소스 정리 메서드
     */
    protected void cleanup() {
        if (driver != null) {
            driver.quit();
            log.info("{} 웹드라이버가 종료되었습니다.", getSiteName());
        }
    }

    /**
     * 특정 요소가 존재하는지 확인하는 유틸리티 메서드
     */
    protected boolean isElementPresent(By locator) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
        shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));

        try {
            driver.findElement(locator);
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }
}