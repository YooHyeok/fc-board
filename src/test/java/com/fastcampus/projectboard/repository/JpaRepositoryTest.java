package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb") // application.yaml에 전역 설정된 testdb profile을 사용한다.
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) //자동으로 testdb를 띄우지않게끔 한다. application.yaml에 전역 설정됨.
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) //Auditing을 위한 Import
@DataJpaTest // slice Test
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;

    JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        long previousCount = articleRepository.count();

        // When
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newYooHyeok", "pw", null, null, null));
        articleRepository.save(Article.of(userAccount, "new article", "new content", "#spring"));

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow(); //없으면 throw후 테스트종료
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        // When
        Article savedArticle = articleRepository.saveAndFlush(article);
        // 자동으로 transactional이 걸려있지만 test에서는 기본값이 rollback으로 동작한다.
        // 저장 이후에 따로 조회를 한다거나 등의 추가적인 작업을 하지 않는다면
        // #springboot라는 데이터는 rollback에 의해서 다시 #spring으로 돌아간다 -> 결과적으로 변경되지 않을거다
        // 따라서 rollback에 의해서 중요하지 않다면 원상태랑 똑같아 진다면 update등의 작업이 생략이 될수 있다.
        // saveAndFlush (저장후 flush한다) flush란 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영한다. SQL이 실제로 동작하게끔 처리하는것.

        // Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag); //hashtag필드의 값이 updatedHashtag의 값과 같은지 조회테스트

    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow(); //없으면 throw후 테스트종료
        long previousArticleCount = articleRepository.count(); //게시글 갯수
        long previousArticleCommentCount = articleCommentRepository.count(); //전체 댓글 갯수
        int deletedCommentSize = article.getArticleComments().size(); //게시글에 달린 댓글 갯수

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount -1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentSize); // 전체 댓글 갯수 : 전체갯수 - 삭제된 게시글 갯수

    }
}
