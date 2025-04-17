package com.helpcentercrawl.crawler.interfaces;

public interface SiteCrawler {
    String getSiteName();
    String getSiteCode();
    void crawl();
}