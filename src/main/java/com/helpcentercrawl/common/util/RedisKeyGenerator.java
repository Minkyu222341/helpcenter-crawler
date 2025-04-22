package com.helpcentercrawl.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * packageName    : com.helpcentercrawl.common.util
 * fileName       : RedisKeyGenerator
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    : Redis에서 사용할 키를 생성하기 위한 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-04-18        MinKyu Park       최초 생성
 */

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