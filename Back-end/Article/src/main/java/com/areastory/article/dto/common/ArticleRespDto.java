package com.areastory.article.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class ArticleRespDto extends ArticleCommonDto {
    private String location;

    @Builder
    public ArticleRespDto(Long articleId, Long userId, String nickname, String profile, String content, String image, Long likeCount, Long commentCount, Boolean isLike, LocalDateTime createdAt, String location) {
        super(articleId, userId, nickname, profile, content, image, likeCount, commentCount, isLike, createdAt);
        this.location = location;
    }
}
