package com.helpcentercrawl.crawler.entity;

import com.helpcentercrawl.crawler.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Comment("사이트 코드")
    private String siteCode;
    @Comment("완료된 요청 개수")
    private Integer completedCount;
    @Comment("미완료된 요청 개수")
    private Integer notCompletedCount;
    @Comment("총 요청 개수")
    private Integer totalCount;

    @Builder
    public CrawlResult(Long id, String siteCode, Integer completedCount, Integer notCompletedCount, Integer totalCount) {
        this.id = id;
        this.siteCode = siteCode;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
    }

    public void updateCounts(Integer completedCount, Integer notCompletedCount, Integer totalCount) {
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
    }

}
