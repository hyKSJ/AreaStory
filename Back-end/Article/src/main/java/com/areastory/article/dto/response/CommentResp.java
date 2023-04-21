package com.areastory.article.dto.response;

import com.areastory.article.dto.common.CommentDto;
import lombok.Builder;

import java.util.List;

@Builder
public class CommentResp {
    private Integer pageSize;
    private Integer totalPageNumber;
    private Long totalCount;
    private List<CommentDto> comments;
}
