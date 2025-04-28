package com.helpcentercrawl.account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * packageName    : com.helpcentercrawl.account.entity
 * fileName       : Account
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    private Long id;
    
    @Comment("사이트 코드")
    private String siteCode;
    
    @Comment("지원센터 로그인 아이디")
    private String loginId;

    @Builder
    public Account(Long id, String siteCode, String loginId) {
        this.id = id;
        this.siteCode = siteCode;
        this.loginId = loginId;
    }
}
