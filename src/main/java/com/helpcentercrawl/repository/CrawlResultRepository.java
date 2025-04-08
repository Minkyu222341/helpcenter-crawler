package com.helpcentercrawl.repository;

import com.helpcentercrawl.entity.CrawlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrawlResultRepository extends JpaRepository<CrawlResult, Long> {
    /**
     * 특정 사이트, 특정 날짜의 크롤링 결과 조회
     */
    @Query("SELECT cr FROM CrawlResult cr WHERE cr.siteCode = :siteCode AND DATE(cr.createdAt) = :date")
    Optional<CrawlResult> findBySiteCodeAndCreatedDate(@Param("siteCode") String siteCode, @Param("date") LocalDate date);

    /**
     * 특정 날짜의 모든 크롤링 결과 조회
     */
    @Query("SELECT cr FROM CrawlResult cr WHERE DATE(cr.createdAt) = :date")
    List<CrawlResult> findAllByCreatedDate(@Param("date") LocalDate date);
}