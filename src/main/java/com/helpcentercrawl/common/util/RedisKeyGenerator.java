package com.helpcentercrawl.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class RedisKeyGenerator implements KeyGenerator {
    public static final String KEY_PREFIX = "crawl:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 키 생성 메서드(siteCode와 날짜를 조합하여 생성)
     */
    @Override
    public String generateKey(String siteCode, LocalDate date) {
        return KEY_PREFIX + siteCode + ":" + date.format(DATE_FORMATTER);
    }

    /**
     * 키 생성 메서드(오늘 날짜만)
     */
    @Override
    public String generateKey(LocalDate date) {
        return KEY_PREFIX + "*:" + date.format(DATE_FORMATTER);
    }
}