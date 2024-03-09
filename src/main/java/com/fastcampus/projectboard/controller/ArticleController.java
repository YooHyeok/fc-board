package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.response.ArticleResponse;
import com.fastcampus.projectboard.response.ArticleWithCommentsResponse;
import com.fastcampus.projectboard.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping // 매핑주소가 베이스 path인 `/articles` 라면 "" 혹은 생략 가능.
    public String articles(
            @RequestParam(required = false) SearchType searchType,
            @RequestParam(required = false) String searchValue,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            ModelMap modelMap
    ) {
        // map을 통해 Article에 대한 모든 정보를 반환용으로만 추출해서 넘긴다.
        modelMap.addAttribute("articles", articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from));
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap modelMap) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticle(articleId));
        modelMap.addAttribute("article", article);
        modelMap.addAttribute("articleComments", article.articleCommentsResponses());
        return "articles/detail";
    }
}
