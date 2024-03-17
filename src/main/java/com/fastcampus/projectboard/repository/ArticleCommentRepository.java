package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.ArticleComment;
import com.fastcampus.projectboard.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>
        , QuerydslPredicateExecutor<ArticleComment> // Generic 일반 T 는 Entity
        , QuerydslBinderCustomizer<QArticleComment> // Generic EntityPath를 상속받은 t 는 QClass
{

    /**
     * Article_Id 의미
     * findByArticle로 ArticleComment 엔티티의 연관관계 필드인 Article을 기준으로 조회하는데
     * 해당 Article 객체의 id필드를 조회하겠다는 의미이다.
     * @param articleId
     * @return
     */
    List<ArticleComment> findByArticle_Id(Long articleId);

    void deleteByIdAndUserAccount_UserId(Long articleCommentId, String userId);
    /**
     * 해당 메소드를 통해 검색에 대한 세부 규칙을 추가
     */
    @Override
//    void customize(QuerydslBindings bindings, QArticle root);
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);

        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
