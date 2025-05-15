package com.helpcentercrawl.crawler.repository;

import com.helpcentercrawl.crawler.entity.CrawlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlResultRepository extends JpaRepository<CrawlResult, Long> , CrawlResultRepositoryCustom {

    /**
     * 사이트 코드로 크롤링 결과 조회
     */
    List<CrawlResult> findBySiteCode(String siteCode);


}