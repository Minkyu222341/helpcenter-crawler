package com.helpcentercrawl.crawler.core;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.entity.CrawlResult;
import com.helpcentercrawl.crawler.entity.enums.RequestStatus;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCrawler implements SiteCrawler {

    protected final CrawlResultService crawlResultService;
    protected final CrawlerValueSettings valueSettings;
    protected WebDriver driver;
    protected WebDriverWait wait;

    // 수집된 요청 정보 저장 목록
    protected List<CrawlResult> crawledRequests = new ArrayList<>();

    private static final String LOCAL_CHROME_DRIVER_PATH = "src/main/resources/chromedriver/windows/chromedriver.exe";
    private static final String PROC_CHROME_DRIVER_PATH = "/usr/bin/chromedriver";
    private static final String SET_PROPERTY = "webdriver.chrome.driver";

    protected String PAGE_COUNT;

    @Override
    public final void crawl() {
        this.PAGE_COUNT = "&listCo=20";
        crawledRequests.clear();

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
            processMultiplePages();

            // 5. DB에 크롤링 결과 저장
            saveData();

        } catch (Exception e) {
            log.error("크롤링 오류: {}", e.getMessage(), e);
        } finally {
            cleanup();
        }
    }

    @Override
    public void crawlAllPages() {
        this.PAGE_COUNT = "&listCo=3000";
        crawledRequests.clear();

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

            // 4. 여러 페이지 데이터 처리
            processMultiplePages();

            // 5. DB에 크롤링 결과 저장
            saveData();

        } catch (Exception e) {
            log.error("페이징 크롤링 오류: {}", e.getMessage(), e);
        } finally {
            cleanup();
        }
    }

    protected void processMultiplePages() {
        processData();

        // 2페이지부터 시작 5페이지까지 크롤링
        for (int currentPage = 2; currentPage <= 5; currentPage++) {
            try {
                // 페이지 링크 존재 확인
                String pageXpath = "//a[text()='" + currentPage + "']";
                List<WebElement> pageLinks = driver.findElements(By.xpath(pageXpath));

                if (pageLinks.isEmpty()) {
                    log.info("{} - {}페이지 링크가 존재하지 않아 크롤링 종료", getSiteName(), currentPage);
                    break;
                }

                // 페이지 링크 클릭
                pageLinks.get(0).click();

                // 테이블 로딩 대기
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(getTableSelector())));

                Thread.sleep(1000);

                // 데이터 처리
                processData();

                Thread.sleep(1000);
            } catch (Exception e) {
                log.info("{} - {}다음 페이지가 존재하지않아 크롤링 종료: {}", getSiteName(), currentPage, e.getMessage());
                break;
            }
        }
    }

    protected void initializeWebDriver() {
        ChromeOptions options = getChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.images", 2);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless", "--no-sandbox", "--disable-dev-shm-usage",
                "--disable-gpu", "--window-size=1920,1080", "--disable-extensions",
                "--disable-infobars", "--blink-settings=imagesEnabled=false");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

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
            log.error("{} 로그인 실패: {}", getSiteName(), e.getMessage());
            throw new RuntimeException("로그인 실패", e);
        }
    }

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
        wait.until(loginModel.getSuccessCondition());
    }

    /**
     * 크롤링
     */
    protected void processData() {
        try {
            String tableSelector = getTableSelector();
            List<WebElement> rows = driver.findElements(By.cssSelector(tableSelector));

            for (WebElement row : rows) {
                try {
                    List<WebElement> cells = row.findElements(By.tagName("td"));

                    if (cells.size() > Math.max(getTitleIndex(), getDateIndex())) {
                        WebElement titleCell = cells.get(getTitleIndex());
                        WebElement dateCell = cells.get(getDateIndex());

                        String titleText;
                        List<WebElement> titleLinks = titleCell.findElements(By.tagName("a"));
                        if (!titleLinks.isEmpty()) {
                            titleText = titleLinks.get(0).getText().trim();
                        } else {
                            titleText = titleCell.getText().trim();
                        }

                        titleText = titleProcessing(titleText);

                        LocalDate date = dateProcessing(dateCell);

                        boolean completed = statusProcessing(row);

                        saveDataBuild(titleText, date, completed);
                    }
                } catch (Exception e) {
                    log.warn("{} - 행 데이터 처리 중 오류 발생: {}", getSiteName(), e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("데이터 처리 오류: {}", e.getMessage());
        }
    }


    private static String titleProcessing(String titleText) {
        titleText = titleText.replace("비밀글", "").trim()
                .replace("새글", "").trim()
                .replace("완료", "").trim()
                .replace("답변완료", "").trim()
                .replace("처리중", "").trim()
                .replace("대기중", "").trim()
                .replace("퍼블중", "").trim()
                .replaceAll("\\[댓글:[0-9]+건]", "").trim()
                .replaceAll("댓글\\s*\\(\\d+\\)", "").trim();
        return titleText;
    }

    private static LocalDate dateProcessing(WebElement dateCell) {
        String dateText = dateCell.getText().trim();
        LocalDate date = null;

        try {
            // YYYY-MM-DD 형식 처리
            if (dateText.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                date = LocalDate.parse(dateText);
            } else {
                // YYYY. MM. DD 또는 YYYY.MM.DD 형식 처리
                String normalizedDate = dateText.replace("-", ". ");
                String[] parts = normalizedDate.split("[.\\s]+");
                if (parts.length >= 3) {
                    int year = Integer.parseInt(parts[0].trim());
                    int month = Integer.parseInt(parts[1].trim());
                    int day = Integer.parseInt(parts[2].trim());
                    date = LocalDate.of(year, month, day);
                }
            }
        } catch (Exception ignored) {
        }
        return date;
    }

    private static boolean statusProcessing(WebElement row) {
        boolean completed = false;
        // 이미지로 확인
        List<WebElement> imgs = row.findElements(By.tagName("img"));
        for (WebElement img : imgs) {
            String alt = img.getAttribute("alt");
            String src = img.getAttribute("src");
            if ((alt != null && (alt.contains("완료"))) ||
                    (src != null && (src.contains("btn_comp") || src.contains("compt")))) {
                completed = true;
                break;
            }
        }

        // 텍스트로 확인
        if (!completed) {
            List<WebElement> spans = row.findElements(By.cssSelector("span"));
            for (WebElement span : spans) {
                String spanText = span.getText().trim();
                if (spanText.equals("완료")) {
                    completed = true;
                    break;
                }
            }
        }
        return completed;
    }

    private void saveDataBuild(String titleText, LocalDate date, boolean completed) {
        if (!titleText.isEmpty() && date != null) {
            CrawlResult result = CrawlResult.builder()
                    .siteCode(getSiteCode())
                    .siteName(getSiteName())
                    .status(completed ? RequestStatus.SUCCESS : RequestStatus.WAIT)
                    .title(titleText)
                    .requestDate(date)
                    .build();
            crawledRequests.add(result);
        }
    }

    /**
     * 중복 제거 후 DB에 저장하는 메서드
     */
    protected void saveData() {
        try {
            if (crawledRequests.isEmpty()) {
                log.info("{} - 저장할 크롤링 결과가 없습니다.", getSiteName());
                return;
            }

            // 중복 비교 데이터 (DB 조회)
            List<CrawlResult> existingData = crawlResultService.findExistingResults(getSiteCode());

            // 중복 제거 (제목, 날짜, 사이트코드 기준)
            List<CrawlResult> newData = crawledRequests.stream()
                    .filter(newResult -> !isDuplicate(newResult, existingData))
                    .collect(Collectors.toList());

            if (newData.isEmpty()) {
                log.info("{} - 모든 데이터가 이미 존재합니다.", getSiteName());
                return;
            }

            List<CrawlResult> savedResults = crawlResultService.saveCrawlResults(newData);
            log.info("{} - DB에 새로운 크롤링 결과 {}건이 저장되었습니다.", getSiteName(), savedResults.size());
        } catch (Exception e) {
            log.error("{} - DB 저장 중 오류 발생: {}", getSiteName(), e.getMessage());
        }
    }

    /**
     * 중복 여부 확인 메서드
     */
    private boolean isDuplicate(CrawlResult newResult, List<CrawlResult> existingResults) {
        return existingResults.stream()
                .anyMatch(existing ->
                        existing.getSiteCode().equals(newResult.getSiteCode()) &&
                                existing.getTitle().equals(newResult.getTitle()) &&
                                existing.getRequestDate().equals(newResult.getRequestDate())
                );
    }

    protected void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected abstract LoginModel getLoginModel();

    protected abstract void accessUrl();

    protected abstract void handlePopup() throws InterruptedException;

    protected abstract void navigateToTargetPage() throws InterruptedException;

    protected abstract String getTableSelector();

    protected abstract int getTitleIndex();

    protected abstract int getDateIndex();

    public abstract String getSiteName();

    public abstract String getSiteCode();
}