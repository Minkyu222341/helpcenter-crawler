package com.helpcentercrawl.repository;

import com.helpcentercrawl.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteRepository extends JpaRepository<Site, Long> {
    // Custom query methods can be defined here if needed
}
