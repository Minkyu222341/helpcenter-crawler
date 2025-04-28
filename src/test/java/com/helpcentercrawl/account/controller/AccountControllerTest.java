package com.helpcentercrawl.account.controller;

import com.helpcentercrawl.account.dto.AccountResponseDto;
import com.helpcentercrawl.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.helpcentercrawl.account.controller
 * fileName       : AccountControllerTest
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .build();
    }

    @Test
    @DisplayName("계정 목록 조회 API가 정상적으로 동작한다")
    void getAccountsTest() throws Exception {
        // given
        AccountResponseDto account1 = AccountResponseDto.builder().id(1L)
                .siteCode("site1")
                .loginId("user1")
                .build();
        AccountResponseDto account2 = AccountResponseDto.builder().id(2L)
                .siteCode("site2")
                .loginId("user2")
                .build();

        List<AccountResponseDto> accounts = Arrays.asList(account1, account2);

        when(accountService.getAccounts()).thenReturn(accounts);

        // when & then
        mockMvc.perform(get("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].siteCode").value("site1"))
                .andExpect(jsonPath("$[0].loginId").value("user1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].siteCode").value("site2"))
                .andExpect(jsonPath("$[1].loginId").value("user2"));

        verify(accountService, times(1)).getAccounts();
    }

    @Test
    @DisplayName("계정 정보가 없을 경우 빈 배열을 반환한다")
    void getEmptyAccountsTest() throws Exception {
        // given
        when(accountService.getAccounts()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(accountService, times(1)).getAccounts();
    }
}
