package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor // lombok의 필수 필드(final)에 대한 생성자를 자동으로 만들어준다.
@Transactional
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
//        return List.of();

        /**
         * SearchType 즉, 검색어가 존재하지 않을때
         * @return findAll의 Page의 제너릭은 JpaRepository의 제너릭 Article이 되므로 map을 통해 ArticleDTO의 from Mapper로 변환
         */
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        /**
         * SearchType 즉, 검색어가 존재할때
         * Eunm을 기준으로 서로 다른 쿼리를 만든다.
         * ex) Title : 제목검색쿼리, ID : 아이디검색쿼리
         * [switch case 화살표 문법]
         * : 콜론은 break해줘야한다.
         *
         */
        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::from);
        };

    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {
        /**
         * getReferenceById : 이전버전의 getOne메소드와 같다.
         * findById를 할 경우 select query가 기본으로 호출되기 때문에 사용했다.
         * 엔티티를 실제로 사용할 때까지 데이터베이스 조회를 지연한다. (ex: 필드 추출 DTO변환)
         * 실제 엔티티 객체가 필요한 시점에서 프록시객체가 아닌 실제 엔티티를 반환한다.
         */
//        Article article = articleRepository.findById(dto.id()).orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + dto.id()));
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if (dto.title() != null) article.setTitle(dto.title());
            if (dto.content() != null) article.setContent(dto.content());
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}", dto); // concatenation 이 아닌 {}문법 interpolation
        }

    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) { // 검색어가 비어있다면
            return Page.empty(pageable);// 빈 페이지를 보여준다.
        }
        return articleRepository.findByHashtag(hashtag,pageable).map(ArticleDto::from);
    }

    public List<String> getHashtags() {
        return articleRepository.findByDistinctHashtags();
    }
}
