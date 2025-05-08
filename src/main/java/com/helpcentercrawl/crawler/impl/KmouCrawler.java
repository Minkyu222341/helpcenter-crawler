package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * packageName    : com.helpcentercrawl.crawler.impl
 * fileName       : KmouCrawler
 * author         : MinKyu Park
 * date           : 25. 5. 2.
 * description    : 해양대학교 지원센터 크롤러
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 2.        MinKyu Park       최초 생성
 */
@Slf4j
@Component
public class KmouCrawler extends AbstractCrawler {

    public KmouCrawler(CrawlResultService crawlResultService, CrawlerValueSettings valueSettings) {
        super(crawlResultService, valueSettings);
    }

    @Override
    protected void accessUrl() {
        driver.get(valueSettings.getKmouLoginUrl());
    }

    @Override
    protected LoginModel getLoginModel() {
        String jsLoginScript =
                "var data = $('#loginForm').serialize();" +
                        "$.ajax({" +
                        "   type: 'POST'," +
                        "   datatype: 'json'," +
                        "   data: data," +
                        "   url: '/kmou/lo/login/login.do'," +
                        "   success: function(data) {" +
                        "       data = JSON.parse(data);" +
                        "       if (data.result == 'Y') {" +
                        "           window.location.href = 'https://www.kmou.ac.kr/apple/main.do';" +
                        "       }" +
                        "   }" +
                        "});";

        return LoginModel.builder()
                .idFieldId("mber_id")
                .pwFieldId("mber_password")
                .loginButtonSelector(null) // JavaScript 로그인 방식이므로 버튼 선택자 불필요
                .username(valueSettings.getKmouUsername())
                .password(valueSettings.getKmouPassword())
                .jsLogin(true)
                .jsLoginScript(jsLoginScript)
                .successCondition(ExpectedConditions.urlContains("apple"))
                .build();
    }

    @Override
    protected void handlePopup() {

    }

    @Override
    protected void navigateToTargetPage() {
        driver.get(valueSettings.getKmouTargetUrl());
        log.info("해양대학교 헬프센터 메인페이지 접속 완료");
    }

    @Override
    protected void processPageData() {
        processMultiplePages("table > tbody > tr");

    }

    @Override
    protected int getMaxPagesCount() {
        int maxPages = 1;
        try {

            List<WebElement> pagination = driver.findElements(By.cssSelector(".BD_paging > a"));

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

    @Override
    public String getSiteName() {
        return valueSettings.getKmouName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getKmouCode();
    }

    @Override
    public Integer getSequence() {
        return 6;
    }
}
