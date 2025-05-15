package com.helpcentercrawl.crawler.interfaces;

/**
 * packageName    : com.helpcentercrawl.crawler.interfaces
 * fileName       : SiteCrawler
 * author         : MinKyu Park
 * date           : 25. 4. 22.
 * description    : 크롤링 기능을 정의하는 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 22.        MinKyu Park       최초 생성
 */

public interface SiteCrawler {
    String getSiteName();
    String getSiteCode();
    Integer getSequence();

    void crawl();
    void crawlAllPages();

}