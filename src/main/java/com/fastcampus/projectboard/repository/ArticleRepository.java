package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * QuerydslPredicateExecutor : 엔티티 안에 있는 모든 필드에 대한 기본 검색 기능을 추가해 준다. <br/>
 * QuerydslBinderCustomizer :　검색에　대한　세부　규칙　기능을　추가할　수　있다．
 */
@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>
        , QuerydslPredicateExecutor<Article> // Generic 일반 T 는 Entity
        , QuerydslBinderCustomizer<QArticle> // Generic EntityPath를 상속받은 t 는 QClass
{

    Page<Article> findByTitle(String title, Pageable pageable);

    /**
     * 해당 메소드를 통해 검색에 대한 세부 규칙을 추가한다.
     * Spring data JPA만을 이용해서 인터페이스만 가지고 기능을 다 사용하기 위해 <br/>
     * default 메소드로 변경하여 리포지토리 레이어에 직접 구현체를 만들지 않고 인터페이스에서 직접 구현한다.
     * @param bindings the {@link QuerydslBindings} to customize, will never be {@literal null}.
     * @param root the entity root, will never be {@literal null}.
     */
    @Override
//    void customize(QuerydslBindings bindings, QArticle root);
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true); // 선택적인 필드만 필터 한다. - true: 리스팅을 하지 않은 properties 검색에서 제외
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy); // 검색을 원하는 field를 추가한다.

        /* exact match에 대한 룰 변경 - first: title의 검색 파라미터는 1개만 받는다. */
//        bindings.bind(root.title).first(SimpleExpression::eq); // 기본 형태 SimpleExpression::eq
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase); // like '${value}: 와일드카드 % 직접 넣는다.(수동옵션)'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase); // like '%${value}% : 대소문자 구분하지 않고 비교'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase); // like '%${value}% : 대소문자 구분하지 않고 비교'
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq); // DataType이 DateTime이다.
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
