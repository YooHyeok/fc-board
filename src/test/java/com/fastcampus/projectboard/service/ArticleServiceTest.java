package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * spring boot slice test기능을 사용하지 않는다.
 * 그렇게 함으로써 spring boot application context가 뜨는데 걸리는 시간을 없앤다.
 * 가능하면 테스트는 가볍게 만드는게 좋기 때문에 굳이 불필요한 springboot의 지원을 받지 않는다.
 * 필요한 dependecy(의존성) 추가에 대한 부분은 Mockito를 사용하여 mocking하여 진행한다.
 */
@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    /**
     * @InjectMocks : 목을 주입하는 대상에 선언 (필드에만 선언)
     * @Mock : 그 외 나머지 모든 목 (타겟 메소드 필드 - 생성자주입 가능)
     */
    @InjectMocks private ArticleService sut; //system under test 테스트 대상이라는 뜻의 네이밍
    @Mock
    private ArticleRepository articleRepository;

    /**
     * 검색 파라미터가 주어지면 게시글들을 검색하고, 게시글들을 리턴한다.
     * (+) 페이지 네이션 , 정렬기능
     */
    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameter_whenSearchingArticles_thenRetrunsArticles() {
        // Given

        // When
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword"); // 검색 파라미터 종류 : 제목, 본문, ID, 닉네임, 해시태그

        // Then
        assertThat(articles).isNotNull();

    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
    @Test
    void givenId_whenSearchingArticle_thenRetrunsArticle() {
        // Given

        // When
        ArticleDto article = sut.searchArticle(1L); // 검색 파라미터 종류 : 제목, 본문, ID, 닉네임, 해시태그

        // Then
        assertThat(article).isNotNull();

    }

    /**
     * [게시글 생성 - solitary테스트]
     * sociable테스트 : 실제로 데이터베이스에 원하는 데이터가 들어갔는지 확인하는 작업은 persistence (DB) 레이어까지 내려가므로 이때부터는 더이상 유닛테스트가 아니다
     * solitary테스트 : 어떤 값을 넣은 것을 실제로 저장이 되어있는가 라는 행위를 하지 않음
     */
    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // Given
        given(articleRepository.save(any(Article.class)))
                .willReturn(null); // saveArticle()를 구현하지 않은 경우 - value를 반환할 것이라는걸 예측하는 용도
//                .willReturn(any(Article.class)); // saveArticle()를 구현한 경우
        // When
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "YooHyeok", "title", "content","#java"));// 저장시 특별한 return은 없고, 에러발생시 에러를 던지면 되기 때문에 void 메소드로 구성

        // Then
        then(articleRepository).should().save(any(Article.class)); // when에 의한 save()호출 여부 확인
    }

    @DisplayName("게시글의 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // Given
        given(articleRepository.save(any(Article.class)))
                .willReturn(null);
//                .willReturn(any(Article.class)); // saveArticle()를 구현한 경우
        // When
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content","#java"));// 저장시 특별한 return은 없고, 에러발생시 에러를 던지면 되기 때문에 void 메소드로 구성

        // Then
        then(articleRepository).should().save(any(Article.class)); // when에 의한 save()호출 여부 확인
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // Given
        willDoNothing() // 아무일도 하지 않기 때문에 사용 - ex) 반환타입 없는 delete가 실제적으로 진행되지 않게하기위함 -> save에서 사용하려면 반환타입이 존재하므로 when().thenReturn()으로 해야한다.
                .given(articleRepository)
                .delete(any(Article.class));
        // When
        sut.deleteArticle(1L);// 저장시 특별한 return은 없고, 에러발생시 에러를 던지면 되기 때문에 void 메소드로 구성

        // Then
        then(articleRepository).should().delete(any(Article.class)); // when에 의한 save()호출 여부 확인
    }


}
