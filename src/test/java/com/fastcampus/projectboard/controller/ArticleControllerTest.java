package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.config.SecurityConfig;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.service.ArticleService;
import com.fastcampus.projectboard.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class) // Security 설정 전체 허용 적용
@WebMvcTest(ArticleController.class) //정석적인 WebMvcTest - 별도의 어노테이션 설정 없이 MockMvc 사용 가능
class ArticleControllerTest {

    /**
     * @WebMvcTest의 @ExtendWith 즉, SpringExtention 내부에 @Autowired를 인지해서
     * 생성자 주입을 할 수 있게끔 하는 코드가 들어있으나, @MockBean은 불가능하다.
     * @Autowired는 타겟이 Method의 parameter에 들어갈 수 있게 되어있으나, MockBean은 타입과 필드만 가능하다.
     */
    @MockBean private ArticleService articleService;
    @MockBean
    private PaginationService paginationService;

    private final MockMvc mvc;

    /**
     * @Autowired Field Injection + Constructor
     * (실제 필드 인젝션을 하면 되지만, 권장히지 않는다는 문구 때문에...)
     * 테스트 패키지에 있는 생성자(주입)은 @Autowired 생략이 불가능하다.
     * @See <a href="https://pinokio0702.tistory.com/189">참조 게시 글</a>
     * @WebMvcTest의 @ExtendWith 즉, SpringExtention 내부에 @Autowired를 인지해서
     * 생성자 주입을 할 수 있게끔 하는 코드가 들어있다.
     */
    ArticleControllerTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        /**
         * eq, any : 일종의 Matcher (Argument Matcher)
         * 정확한 null, 어떠한(아무) Pageable 클래스
         */
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4)); //임의의 값들로 페이징 테스트

        // When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk()) //status 200 여부
//                .andExpect(content().contentType(MediaType.TEXT_HTML)) //내용의 타입 view이므로 TEXT-HTML인지 여부
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) //호환되는 타입까지 적용시켜주는 옵션이 있다 ex) 스프링부트에서 추가시켜주는 charset=UTF-8
                .andExpect(view().name("articles/index")) //해당 경로에 view가 있어야함
                /* 해당 뷰는 데이터가 있어야한다. 게시글 목록이 출력된다는것은 서버에서 게시글 목록을 ModelAttribute로 view에 넘겨줬다는 의미 */
                .andExpect(model().attributeExists("articles")) //모델 애트리뷰트맵에 해당이름의 키가 있는지 여부
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class)); // should는 1회 호출된다는 내용이 포함되어있음.
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // Given
        Long articleId = 1L;
        long totalCount = 1L; // 임의의 값 (0이어도 됨...)
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);
        // When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk()) //status 200 여부
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments")) // 댓글 ModelAttribute 야부 확인
                .andExpect(model().attribute("totalCount", totalCount));
        then(articleService).should().getArticle(articleId);
        then(articleService).should().getArticleCount();
    }

    @Disabled("구현중")
    @DisplayName("[view][GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk()) //status 200 여부
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search"));

    }

    @Disabled("구현중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk()) //status 200 여부
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/search-hashtag"));
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "yooHyeok",
                LocalDateTime.now(),
                "yooHyeok"
        );
    }
    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "yooHyeok",
                "123qwe",
                "webdevyoo@gmail.com",
                "yooHyeok",
                "memo",
                LocalDateTime.now(),
                "yooHyeok",
                LocalDateTime.now(),
                "yooHyeok"
        );
    }
}
