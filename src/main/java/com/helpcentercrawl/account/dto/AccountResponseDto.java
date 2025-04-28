package com.helpcentercrawl.account.dto;

import com.helpcentercrawl.account.entity.Account;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.helpcentercrawl.account.dto
 * fileName       : AccountResponseDto
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Getter
@Builder
public class AccountResponseDto {
    private Long id;
    private String siteCode;
    private String loginId;

    public static AccountResponseDto entityToDto(Account account) {
        return AccountResponseDto.builder()
                .siteCode(account.getSiteCode())
                .loginId(account.getLoginId())
                .build();
    }
}
