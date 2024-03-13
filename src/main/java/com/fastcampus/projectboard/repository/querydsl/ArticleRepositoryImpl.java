package com.fastcampus.projectboard.repository.querydsl;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.QArticle;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ArticleRepositoryImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findByDistinctHashtags() {
        QArticle article = QArticle.article;

        return from(article)//from만 지원하므로 from부터 시작한다.
                .distinct() // 중복 제거
                .select(article.hashtag) //hashtag만 스칼라값으로 조회해온다.
                .where(article.hashtag.isNotNull()) // null값을 제외
                .fetch();
    }
}
