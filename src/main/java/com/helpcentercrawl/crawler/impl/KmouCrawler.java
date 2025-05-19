package com.helpcentercrawl.crawler.impl;

import com.helpcentercrawl.common.config.CrawlerValueSettings;
import com.helpcentercrawl.crawler.core.AbstractCrawler;
import com.helpcentercrawl.crawler.model.LoginModel;
import com.helpcentercrawl.crawler.service.CrawlResultService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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

    private static final int TITLE_INDEX = 2;
    private static final int DATE_INDEX = 4;
    private static final String TABLE_SELECTOR = "tbody > tr";

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
        String targetUrl = UriComponentsBuilder.fromUriString(valueSettings.getKmouTargetUrl())
                .queryParam(QUERY_PARAM_NAME, PARAM_PAGE_COUNT)
                .build()
                .toUriString();

        driver.get(targetUrl);
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
        return valueSettings.getKmouName();
    }

    @Override
    public String getSiteCode() {
        return valueSettings.getKmouCode();
    }
}
