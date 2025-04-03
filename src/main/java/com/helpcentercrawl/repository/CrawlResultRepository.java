package com.helpcentercrawl.repository;

import com.helpcentercrawl.entity.CrawlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlResultRepository extends JpaRepository<CrawlResult, Long> {
    // Custom query methods can be defined here if needed
}
