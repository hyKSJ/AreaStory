package com.areastory.article.db.repository.custom;

import com.areastory.article.dto.common.ArticleDto;
import com.areastory.article.dto.common.CommentDto;
import com.areastory.article.dto.request.ArticleReq;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.areastory.article.db.entity.QArticle.article;
import static com.areastory.article.db.entity.QArticleLike.articleLike;
import static com.areastory.article.db.entity.QComment.comment;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;


@Repository
@RequiredArgsConstructor
public class ArticleCustomRepositoryImpl implements ArticleCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<ArticleDto> findAll(ArticleReq articleReq, Pageable pageable) {
//        List<ArticleDto> test = query.selectFrom(article)
//                .leftJoin(comment)
//                .on(comment.article.eq(article))
//                .leftJoin(articleLike)
//                .on(articleLike.user.userId.eq(articleReq.getUserId()), articleLike.article.eq(article))
//                .where(containsDo(articleReq.getDoName()), containsSi(articleReq.getSi()), containsGun(articleReq.getGun()),
//                        containsGu(articleReq.getGu()), containsDong(articleReq.getDong()), containsEup(articleReq.getEup()),
//                        containsMyeon(articleReq.getMyeon()))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .transform(
//                        groupBy(article.articleId).list(
//                                Projections.constructor(ArticleDto.class,
//                                        list(
//                                                Projections.constructor(CommentDto.class,
//                                                        comment.user.nickname,
//                                                        comment.user.profile,
//                                                        comment.content,
//                                                        comment.likeCount)
//                                        ), article.articleId,
//                                        article.user.nickname,
//                                        article.user.profile,
//                                        article.content,
//                                        article.image,
//                                        article.likeCount,
//                                        article.commentCount,
//                                        new CaseBuilder()
//                                                .when(articleLike.user.userId.eq(articleReq.getUserId()))
//                                                .then(true)
//                                                .otherwise(false))
//                        )
//                );


//        List<ArticleDto> test = query.selectFrom(comment)
//                .rightJoin(article)
//                .on(comment.article.eq(article))
//                .leftJoin(articleLike)
//                .on(articleLike.user.userId.eq(articleReq.getUserId()), articleLike.article.eq(article))
//                .where(eqDo(articleReq.getDoName()), eqSi(articleReq.getSi()), eqGun(articleReq.getGun()),
//                        eqGu(articleReq.getGu()), eqDong(articleReq.getDong()), eqEup(articleReq.getEup()),
//                        eqMyeon(articleReq.getMyeon()))
////                .offset(pageable.getOffset())
////                .limit(2)
////                .orderBy(comment.article.likeCount.desc())
//                .transform(
//                        groupBy(article.articleId).list(
//                                Projections.constructor(ArticleDto.class, list(
//                                                Projections.constructor(CommentDto.class,
//                                                        comment.user.nickname,
//                                                        comment.user.profile,
//                                                        comment.content,
//                                                        comment.likeCount)
//                                        ), article.articleId,
//                                        article.user.nickname,
//                                        article.user.profile,
//                                        article.content,
//                                        article.image,
//                                        article.likeCount,
//                                        article.commentCount,
//                                        new CaseBuilder()
//                                                .when(articleLike.user.userId.eq(articleReq.getUserId()))
//                                                .then(true)
//                                                .otherwise(false))
//                        )
//                );

        List<ArticleDto> test = query.selectFrom(article)
                .leftJoin(articleLike)
                .on(articleLike.user.userId.eq(articleReq.getUserId()), articleLike.article.eq(article))
                .leftJoin(comment)
                .on(comment.article.eq(article))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .transform(
                        groupBy(article.articleId).list(
                                Projections.constructor(ArticleDto.class, list(
                                                Projections.constructor(CommentDto.class,
                                                        comment.user.nickname,
                                                        comment.user.profile,
                                                        comment.content,
                                                        comment.likeCount)
                                        ), article.articleId,
                                        article.user.nickname,
                                        article.user.profile,
                                        article.content,
                                        article.image,
                                        article.likeCount,
                                        article.commentCount,
                                        new CaseBuilder()
                                                .when(articleLike.user.userId.eq(articleReq.getUserId()))
                                                .then(true)
                                                .otherwise(false))
                        )
                );

        JPAQuery<Long> articleSize = query
                .select(article.count())
                .from(article);

//        System.out.println("articleSize: " + articleSize);
        return PageableExecutionUtils.getPage(test, pageable, articleSize::fetchOne);
    }

    @Override
    public ArticleDto findById(Long userId, Long articleId) {
        ArticleDto articleDetail = query
                .select(Projections.constructor(ArticleDto.class,
                        article.articleId,
                        article.user.nickname,
                        article.user.profile,
                        article.content,
                        article.image,
                        article.likeCount,
                        article.commentCount,
                        new CaseBuilder()
                                .when(articleLike.user.userId.eq(userId))
                                .then(true)
                                .otherwise(false)
                ))
                .from(article)
                .leftJoin(articleLike)
                .on(articleLike.user.userId.eq(userId), articleLike.article.eq(article))
                .where(article.articleId.eq(articleId))
                .fetchOne();

        articleDetail.setComment(CommentList(articleId, 10L, comment.likeCount.desc()));
        return articleDetail;
    }

    private List<CommentDto> CommentList(Long articleId, Long limit, OrderSpecifier<Long> sort) {
        return query
                .select(Projections.constructor(CommentDto.class,
                        comment.content,
                        comment.likeCount,
                        comment.user.nickname,
                        comment.user.profile
                ))
                .from(comment)
                .where(comment.article.articleId.eq(articleId))
                .orderBy(sort)
                .limit(limit)
                .fetch();
    }

    private BooleanExpression eqDo(String doName) {
        if (StringUtils.hasText(doName)) {
            return article.doName.eq(doName);
        }
        return null;
    }

    private BooleanExpression eqSi(String si) {
        if (StringUtils.hasText(si)) {
            return article.si.eq(si);
        }
        return null;
    }

    private BooleanExpression eqGun(String gun) {
        if (StringUtils.hasText(gun)) {
            return article.gun.eq(gun);
        }
        return null;
    }

    private BooleanExpression eqGu(String gu) {
        if (StringUtils.hasText(gu)) {
            return article.gu.eq(gu);
        }
        return null;
    }

    private BooleanExpression eqDong(String dong) {
        if (StringUtils.hasText(dong)) {
            return article.dong.eq(dong);
        }
        return null;
    }

    private BooleanExpression eqEup(String eup) {
        if (StringUtils.hasText(eup)) {
            return article.eup.eq(eup);
        }
        return null;
    }

    private BooleanExpression eqMyeon(String myeon) {
        if (StringUtils.hasText(myeon)) {
            return article.myeon.eq(myeon);
        }
        return null;
    }
}
