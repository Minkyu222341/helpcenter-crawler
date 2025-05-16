package com.helpcentercrawl.status.entity;

import com.helpcentercrawl.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * packageName    : com.helpcentercrawl.status.entity
 * fileName       : CrawlerStatus
 * author         : MinKyu Park
 * date           : 25. 5. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 9.        MinKyu Park       최초 생성
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlerStatus extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("시퀀스")
    private Long id;

    @Column(nullable = false, unique = true)
    @Comment("사이트 코드")
    private String siteCode;

    @Column(nullable = false)
    @Comment("사이트 이름")
    private String siteName;

    @Comment("활성화 상태 (true: 활성화, false: 비활성화)")
    private boolean enabled = true;

    @Comment("화면 노출 순서")
    private int viewSequence;

    @Comment("마지막으로 크롤링 된 시간")
    private LocalDateTime crawledAt;

    @Builder
    public CrawlerStatus(Long id, String siteCode, String siteName, boolean enabled, int viewSequence, LocalDateTime crawledAt) {
        this.id = id;
        this.siteCode = siteCode;
        this.siteName = siteName;
        this.enabled = enabled;
        this.viewSequence = viewSequence;
        this.crawledAt = crawledAt;
    }

    public void updateStatus(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateCrawledAt(LocalDateTime crawledAt) {
        this.crawledAt = crawledAt;
    }
}
