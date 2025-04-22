package com.helpcentercrawl.common.util;

import java.time.LocalDate;

/**
 * packageName    : com.helpcentercrawl.common.util
 * fileName       : KeyGenerator
 * author         : MinKyu Park
 * date           : 2025-04-18
 * description    : Redis에서 사용할 키를 생성하기 위한 인터페이스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-04-18        MinKyu Park       최초 생성
 */

public interface KeyGenerator {
    String generateKey(String siteCode, LocalDate date);
    String generateKey(LocalDate date);
}
