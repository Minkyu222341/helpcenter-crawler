package com.helpcentercrawl.crawler.repository;

import com.helpcentercrawl.crawler.entity.CrawlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * packageName    : com.helpcentercrawl.crawler.repository
 * fileName       : CrawlResultRepository
 * author         : MinKyu Park
 * date           : 25. 4. 21.
 * description    : 크롤링 결과를 저장하는 Repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 21.        MinKyu Park       최초 생성
 */

@Repository
public interface CrawlResultRepository extends JpaRepository<CrawlResult, Long> {

    // 날짜 비교를 위한 메소드
    @Query("SELECT COUNT(c) FROM CrawlResult c WHERE c.createdAt >= :startDate AND c.createdAt < :endDate")
    long countByTheDayBefore(LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Query("DELETE FROM CrawlResult c WHERE c.createdAt >= :startDate AND c.createdAt < :endDate")
    void deleteByExistingData(LocalDateTime startDate, LocalDateTime endDate);
}
