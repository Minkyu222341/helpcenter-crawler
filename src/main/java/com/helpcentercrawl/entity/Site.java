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
public class Site extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사이트 이름")
    private String name;

    @Comment("사이트 코드")
    private String code;

    @Builder
    public Site(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
