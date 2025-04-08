package com.helpcentercrawl.crawler.domain;

public interface SiteCrawler {
    String getSiteName();
    String getSiteCode();
    void crawl();
}