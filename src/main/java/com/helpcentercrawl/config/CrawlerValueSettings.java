package com.helpcentercrawl.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class CrawlerValueSettings {

    @Value("${crawler.chrome-driver.path}")
    private String chromeDriverPath;

    // 경남 사이트 설정
    @Value("${crawler.sites.gne.code}")
    private String gneCode;

    @Value("${crawler.sites.gne.name}")
    private String gneName;

    @Value("${crawler.sites.gne.login-url}")
    private String gneLoginUrl;

    @Value("${crawler.sites.gne.target-url}")
    private String gneTargetUrl;

    @Value("${crawler.sites.gne.username}")
    private String gneUsername;

    @Value("${crawler.sites.gne.password}")
    private String gnePassword;

    // 창원 사이트 설정
    @Value("${crawler.sites.changwon.code}")
    private String changwonCode;

    @Value("${crawler.sites.changwon.name}")
    private String changwonName;

    @Value("${crawler.sites.changwon.login-url}")
    private String changwonLoginUrl;

    @Value("${crawler.sites.changwon.target-url}")
    private String changwonTargetUrl;

    @Value("${crawler.sites.changwon.username}")
    private String changwonUsername;

    @Value("${crawler.sites.changwon.password}")
    private String changwonPassword;

    // 부산교육청-학교 사이트 설정
    @Value("${crawler.sites.busan-school.code}")
    private String busanSchoolCode;
    @Value("${crawler.sites.busan-school.name}")
    private String busanSchoolName;

    @Value("${crawler.sites.busan-school.login-url}")
    private String busanSchoolLoginUrl;

    @Value("${crawler.sites.busan-school.school-target-url}")
    private String busanSchoolSchoolTargetUrl;

    @Value("${crawler.sites.busan-school.kindergarten-target-url}")
    private String busanSchoolKindergartenTargetUrl;

    @Value("${crawler.sites.busan-school.username}")
    private String busanSchoolUsername;

    @Value("${crawler.sites.busan-school.password}")
    private String busanSchoolPassword;
}