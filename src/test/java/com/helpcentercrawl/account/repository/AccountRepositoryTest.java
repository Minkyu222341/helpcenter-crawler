package com.helpcentercrawl.account.repository;

import com.helpcentercrawl.account.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.helpcentercrawl.account.repository
 * fileName       : AccountRepositoryTest
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("findAll 메서드는 저장된 모든 계정을 조회한다")
    @Sql("/sql/account-test-data.sql")
    void findAllTest() {
        // when
        List<Account> accounts = accountRepository.findAll();

        // then
        assertThat(accounts).isNotEmpty();
        assertThat(accounts).hasSize(3);
        assertThat(accounts.get(0).getSiteCode()).isEqualTo("site1");
    }

    // 추가 테스트 메서드
    @Test
    @DisplayName("Repository가 비어있을 때 findAll은 빈 리스트를 반환한다")
    void findAllEmptyTest() {
        // given
        accountRepository.deleteAll(); // 기존 데이터 모두 삭제

        // when
        List<Account> accounts = accountRepository.findAll();

        // then
        assertThat(accounts).isEmpty();
    }
}