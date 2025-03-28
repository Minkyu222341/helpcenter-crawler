package com.helpcentercrawl.crawler.core;

import com.helpcentercrawl.crawler.domain.SiteCrawler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 헬프센터 크롤링을 위한 추상 클래스
 * 템플릿 메서드 패턴을 적용하여 공통 로직 추상화
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCrawler implements SiteCrawler {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected LocalDate today;
    protected AtomicInteger todayTotal = new AtomicInteger(0);
    protected AtomicInteger todayCompleted = new AtomicInteger(0);
    protected AtomicInteger todayNotCompleted = new AtomicInteger(0);

    /**
     * 템플릿 메서드 - 크롤링 전체 프로세스를 정의합니다.
     */
    @Override
    public final void crawl() {
        // 크롬 드라이버 경로 설정
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver/chromedriver.exe");

        try {
            // 1. 웹드라이버 초기화
            initializeWebDriver();

            // 2. 로그인 처리
            login();

            // 3. 대상 페이지로 이동
            navigateToTargetPage();

            // 4. 페이지에서 데이터 추출 및 처리
            processPageData();

            // 5. 결과 출력
            printResults();

        } catch (Exception e) {
            log.error("크롤링 중 오류 발생: {}", e.getMessage());
            log.error("오류 상세 정보:", e);
        } finally {
            // 6. 리소스 정리
            cleanup();
        }
    }

    /**
     * 웹드라이버 초기화 메서드
     */
    protected void initializeWebDriver() {
        ChromeOptions options = getChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        today = LocalDate.now();
        log.info("{} 웹드라이버 초기화 완료", getSiteName());
    }

    /**
     * 크롬 옵션 설정 메서드
     */
    protected ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--blink-settings=imagesEnabled=false");

        // 페이지 로드 전략 설정
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // 성능 매개변수 설정
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.images", 2);
        options.setExperimentalOption("prefs", prefs);

        return options;
    }

    /**
     * 로그인 처리 메서드
     */
    protected abstract void login() throws InterruptedException;

    /**
     * 대상 페이지로 이동하는 메서드
     */
    protected abstract void navigateToTargetPage();

    /**
     * 페이지 데이터 처리 메서드
     */
    protected abstract void processPageData() throws InterruptedException;

    /**
     * 단일 페이지의 데이터 처리를 위한 공통 메서드
     * @param selector 테이블 행을 선택하기 위한 CSS 선택자
     */
    protected void processSinglePage(String selector) {
        // 테이블 행 가져오기
        List<WebElement> rows = driver.findElements(By.cssSelector(selector));

        // 행 처리 공통 메서드 호출
        processRows(rows);
    }

    /**
     * 여러 페이지의 데이터를 처리하는 공통 메서드
     * @param tableSelector 테이블 행 선택자
     * @param paginationSelector 페이지네이션 선택자
     * @param maxPageToCheck 확인할 최대 페이지 수
     */
    protected void processMultiplePages(String tableSelector, String paginationSelector, int maxPageToCheck){
        // 페이지네이션 탐색
        int maxPages = getMaxPagesCount(paginationSelector);

        // 각 페이지 탐색
        for (int currentPage = 1; currentPage <= Math.min(maxPages, maxPageToCheck); currentPage++) {
            if (currentPage > 1) {
                navigateToPage(currentPage);
            }

            // 단일 페이지 처리
            processSinglePage(tableSelector);
        }
    }

    /**
     * 페이지 수 확인 공통 메서드
     */
    protected int getMaxPagesCount(String paginationSelector) {
        int maxPages = 1;
        try {
            // 페이지네이션 요소 찾기
            List<WebElement> pagination = driver.findElements(By.cssSelector(paginationSelector));

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
     * 특정 페이지로 이동하는 메서드
     */
    protected void navigateToPage(int pageNumber) {
        try {
            // 페이지 번호를 가진 요소 찾기 시도
            String pageXpath = "//a[text()='" + pageNumber + "']";
            WebElement pageLink = driver.findElement(By.xpath(pageXpath));
            pageLink.click();

            // 페이지 로딩 대기
            waitForPageLoad();
        } catch (Exception e) {
            log.error("{}페이지로 이동 중 오류 발생: {}", pageNumber, e.getMessage());
            throw e;
        }
    }

    /**
     * 결과 출력 메서드
     */
    protected void printResults() {
        System.out.println("\n===== " + getSiteName() + " 오늘 요청 통계 =====");
        System.out.println("오늘 들어온 요청의 개수: " + todayTotal.get() + "건");
        System.out.println("완료처리된 개수: " + todayCompleted.get() + "건");
        System.out.println("완료처리가 아닌 개수: " + todayNotCompleted.get() + "건");
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
     * 페이지 완전 로딩 대기 유틸리티 메서드
     */
    protected void waitForPageLoad() {
        try {
            wait.until(webDriver ->
                    Objects.equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState"), "complete"));
            // 추가 대기 시간 - AJAX 로딩 등을 위한 시간
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 테이블 행을 처리하는 공통 메서드
     * @param rows 처리할 웹 요소(행) 목록
     */
    protected void processRows(List<WebElement> rows) {
        for (WebElement row : rows) {
            try {
                // 날짜 텍스트 추출
                String dateText = extractDateText(row);
                if (dateText.isEmpty()) continue; // 날짜를 찾지 못하면 건너뛰기

                // 오늘 날짜인 경우만 처리
                if (isToday(dateText)) {
                    todayTotal.incrementAndGet(); // 총 개수 증가

                    // 완료 상태 확인
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
     * 오늘 날짜인지 확인하는 유틸리티 메서드
     */
    protected boolean isToday(String dateText) {
        try {
            // 날짜 포맷이 다양할 수 있으므로 여러 방식 시도

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
}