package com.helpcentercrawl.entity;

import com.helpcentercrawl.entity.base.BaseEntity;
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
    @Comment("사이트 Idx")
    private Long siteIdx;
    @Comment("완료된 요청 개수")
    private Integer completedCount;
    @Comment("미완료된 요청 개수")
    private Integer notCompletedCount;
    @Comment("총 요청 개수")
    private Integer totalCount;
    @Comment("요청사항 평균 처리 시간(분)")
    private Long averageProcessingTimeMinutes;

    @Builder
    public CrawlResult(Long id, Long siteIdx, Integer completedCount, Integer notCompletedCount, Integer totalCount, Long averageProcessingTimeMinutes) {
        this.id = id;
        this.siteIdx = siteIdx;
        this.completedCount = completedCount;
        this.notCompletedCount = notCompletedCount;
        this.totalCount = totalCount;
        this.averageProcessingTimeMinutes = averageProcessingTimeMinutes;
    }
}
