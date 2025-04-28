package com.helpcentercrawl.account.service;

import com.helpcentercrawl.account.dto.AccountResponseDto;
import com.helpcentercrawl.account.entity.Account;
import com.helpcentercrawl.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.helpcentercrawl.account.service
 * fileName       : AccountService
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;


    /**
     * 계정 목록 조회
     * @return List<AccountResponseDto>
     */
    public List<AccountResponseDto> getAccounts() {

        List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map(AccountResponseDto::entityToDto)
                .toList();
    }
}
