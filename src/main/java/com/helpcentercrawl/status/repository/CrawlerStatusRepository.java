package com.helpcentercrawl.status.repository;

import com.helpcentercrawl.status.entity.CrawlerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.helpcentercrawl.status.repository
 * fileName       : CrawlerStatusRepository
 * author         : MinKyu Park
 * date           : 25. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 9.        MinKyu Park       최초 생성
 */
@Repository
public interface CrawlerStatusRepository extends JpaRepository<CrawlerStatus, Long> {

    Optional<CrawlerStatus> findBySiteCode(String siteCode);
    List<CrawlerStatus> findBySiteCodeIn(List<String> siteCodes);
}