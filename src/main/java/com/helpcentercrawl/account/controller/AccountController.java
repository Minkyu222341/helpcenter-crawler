package com.helpcentercrawl.account.controller;

import com.helpcentercrawl.account.dto.AccountResponseDto;
import com.helpcentercrawl.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * packageName    : com.helpcentercrawl.account.controller
 * fileName       : AccountController
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    /**
     * 계정 목록 조회
     * @return List<AccountResponseDto>
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseDto>> getAccounts() {
        return ResponseEntity.ok(accountService.getAccounts());
    }

}
