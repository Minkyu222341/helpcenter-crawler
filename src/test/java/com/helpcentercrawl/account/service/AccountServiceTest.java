package com.helpcentercrawl.account.service;

import com.helpcentercrawl.account.dto.AccountResponseDto;
import com.helpcentercrawl.account.entity.Account;
import com.helpcentercrawl.account.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.helpcentercrawl.account.service
 * fileName       : AccountServiceTest
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("모든 계정 정보를 조회한다")
    void getAllAccounts() {
        // given
        Account account1 = createAccount(1L, "site1", "user1");
        Account account2 = createAccount(2L, "site2", "user2");
        List<Account> accounts = Arrays.asList(account1, account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        // when
        List<AccountResponseDto> result = accountService.getAccounts();

        // then
        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getSiteCode()).isEqualTo("site1");
        assertThat(result.get(0).getLoginId()).isEqualTo("user1");
//        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getSiteCode()).isEqualTo("site2");
        assertThat(result.get(1).getLoginId()).isEqualTo("user2");

        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("계정 정보가 없을 경우 빈 리스트를 반환한다")
    void getEmptyAccounts() {
        // given
        when(accountRepository.findAll()).thenReturn(List.of());

        // when
        List<AccountResponseDto> result = accountService.getAccounts();

        // then
        assertThat(result).isEmpty();
        verify(accountRepository, times(1)).findAll();
    }

    private Account createAccount(Long id, String siteCode, String loginId) {
        return Account.builder()
                .id(id)
                .siteCode(siteCode)
                .loginId(loginId)
                .build();
    }
}