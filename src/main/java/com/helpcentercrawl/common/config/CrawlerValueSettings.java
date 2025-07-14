package com.helpcentercrawl.common.config;

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

    // 서울교대 사이트 설정
    @Value("${crawler.sites.snue.code}")
    private String snueCode;
    @Value("${crawler.sites.snue.name}")
    private String snueName;
    @Value("${crawler.sites.snue.login-url}")
    private String snueLoginUrl;
    @Value("${crawler.sites.snue.target-url}")
    private String snueTargetUrl;
    @Value("${crawler.sites.snue.username}")
    private String snueUsername;
    @Value("${crawler.sites.snue.password}")
    private String snuePassword;

    // 부산 행정 사이트 설정
    @Value("${crawler.sites.busan-admin.code}")
    private String busanAdminCode;
    @Value("${crawler.sites.busan-admin.name}")
    private String busanAdminName;
    @Value("${crawler.sites.busan-admin.login-url}")
    private String busanAdminLoginUrl;
    @Value("${crawler.sites.busan-admin.target-url}")
    private String busanAdminTargetUrl;
    @Value("${crawler.sites.busan-admin.username}")
    private String busanAdminUsername;
    @Value("${crawler.sites.busan-admin.password}")
    private String busanAdminPassword;

    //부산 본청 사이트 설정
    @Value("${crawler.sites.busan-main.code}")
    private String busanMainCode;
    @Value("${crawler.sites.busan-main.name}")
    private String busanMainName;
    @Value("${crawler.sites.busan-main.login-url}")
    private String busanMainLoginUrl;
    @Value("${crawler.sites.busan-main.target-url}")
    private String busanMainTargetUrl;
    @Value("${crawler.sites.busan-main.username}")
    private String busanMainUsername;
    @Value("${crawler.sites.busan-main.password}")
    private String busanMainPassword;


    //경상국립대 사이트 설정
    @Value("${crawler.sites.gnu.code}")
    private String gnuCode;
    @Value("${crawler.sites.gnu.name}")
    private String gnuName;
    @Value("${crawler.sites.gnu.login-url}")
    private String gnuLoginUrl;
    @Value("${crawler.sites.gnu.main-target-url}")
    private String gnuMainTargetUrl;
    @Value("${crawler.sites.gnu.sub-target-url}")
    private String gnuSubTargetUrl;
    @Value("${crawler.sites.gnu.username}")
    private String gnuUsername;
    @Value("${crawler.sites.gnu.password}")
    private String gnuPassword;

    //해양대학교 사이트 설정
    @Value("${crawler.sites.kmou.code}")
    private String kmouCode;
    @Value("${crawler.sites.kmou.name}")
    private String kmouName;
    @Value("${crawler.sites.kmou.login-url}")
    private String kmouLoginUrl;
    @Value("${crawler.sites.kmou.target-url}")
    private String kmouTargetUrl;
    @Value("${crawler.sites.kmou.username}")
    private String kmouUsername;
    @Value("${crawler.sites.kmou.password}")
    private String kmouPassword;

    //늘봄학교 서비스 사이트 설정
    @Value("${crawler.sites.care.code}")
    private String careCode;
    @Value("${crawler.sites.care.name}")
    private String careName;
    @Value("${crawler.sites.care.login-url}")
    private String careLoginUrl;
    @Value("${crawler.sites.care.target-url}")
    private String careTargetUrl;
    @Value("${crawler.sites.care.username}")
    private String careUsername;
    @Value("${crawler.sites.care.password}")
    private String carePassword;

    //전세임대포털 사이트 설정
    @Value("${crawler.sites.jeonse.code}")
    private String jeonseCode;
    @Value("${crawler.sites.jeonse.name}")
    private String jeonseName;
    @Value("${crawler.sites.jeonse.login-url}")
    private String jeonseLoginUrl;
    @Value("${crawler.sites.jeonse.target-url}")
    private String jeonseTargetUrl;
    @Value("${crawler.sites.jeonse.username}")
    private String jeonseUsername;


}