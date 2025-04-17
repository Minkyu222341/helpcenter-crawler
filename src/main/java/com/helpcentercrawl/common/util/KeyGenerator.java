package com.helpcentercrawl.common.util;

import java.time.LocalDate;

public interface KeyGenerator {
    String generateKey(String siteCode, LocalDate date);
    String generateKey(LocalDate date);
}
