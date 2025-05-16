package com.helpcentercrawl.crawler.entity.enums;

import lombok.Getter;

/**
 * packageName    : com.helpcentercrawl.crawler.entity.enums
 * fileName       : RequestStatus
 * author         : MinKyu Park
 * date           : 25. 5. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 12.        MinKyu Park       최초 생성
 */
@Getter
public enum RequestStatus {
    SUCCESS("완료"), WAIT("대기");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

}
