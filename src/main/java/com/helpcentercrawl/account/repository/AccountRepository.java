package com.helpcentercrawl.account.repository;

import com.helpcentercrawl.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * packageName    : com.helpcentercrawl.account.repository
 * fileName       : AccountRepository
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
