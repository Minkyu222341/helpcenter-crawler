package com.helpcentercrawl.crawler.entity;

import com.helpcentercrawl.common.entity.BaseEntity;
import com.helpcentercrawl.crawler.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사이트 코드")
    @Column(nullable = false)
    private String siteCode;

    @Comment("요청 처리 상태")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Comment("요청 제목")
    @Column(nullable = false)
    private String title;

    @Comment("요청 날짜")
    @Column(nullable = false)
    private LocalDate requestDate;

    @Builder
    public CrawlResult(Long id, String siteCode, String siteName, RequestStatus status, String title, LocalDate requestDate) {
        this.id = id;
        this.siteCode = siteCode;
        this.status = status;
        this.title = title;
        this.requestDate = requestDate;
    }
}
