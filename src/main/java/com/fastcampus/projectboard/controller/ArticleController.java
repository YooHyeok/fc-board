package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.domain.constant.FormStatus;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.request.ArticleRequest;
import com.fastcampus.projectboard.response.ArticleResponse;
import com.fastcampus.projectboard.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard.service.ArticleService;
import com.fastcampus.projectboard.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 */
@RequiredArgsConstructor
@RequestMapping("/articles") // 모든 view EndPoint들이 article로 시작된다. - base 매핑주소
@Controller
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;


    @GetMapping // 매핑주소가 베이스 path인 `/articles` 라면 "" 혹은 생략 가능.
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap modelMap
    ) {
        // map을 통해 Article에 대한 모든 정보를 반환용으로만 추출해서 넘긴다.

        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        modelMap.addAttribute("articles", articles);
        modelMap.addAttribute("paginationBarNumbers", barNumbers);
        modelMap.addAttribute("searchTypes", searchType.values());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap modelMap) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        modelMap.addAttribute("article", article);
        modelMap.addAttribute("articleComments", article.articleCommentsResponses());
        modelMap.addAttribute("totalCount", articleService.getArticleCount());
        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchArticleHashtag(@RequestParam(required = false) String searchValue,
                                @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                ModelMap modelMap
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        modelMap.addAttribute("articles", articles);
        modelMap.addAttribute("hashtags", hashtags);
        modelMap.addAttribute("paginationBarNumbers", barNumbers);
        modelMap.addAttribute("searchType", SearchType.HASHTAG);
        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "articles/form";
    }

    @PostMapping ("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.saveArticle(articleRequest.toDto(UserAccountDto.of(
                "yooHyeok", "123qwe", "webdevyoo@mail.com", "yooHyeok", "memo", null, null, null, null
        )));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping ("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.updateArticle(articleId, articleRequest.toDto(UserAccountDto.of(
                "yooHyeok", "123qwe", "webdevyoo@mail.com", "yooHyeok", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + articleId;
    }

    @PostMapping ("/{articleId}/delete")
    public String deleteArticle(@PathVariable Long articleId) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleService.deleteArticle(articleId);

        return "redirect:/articles";
    }
}
