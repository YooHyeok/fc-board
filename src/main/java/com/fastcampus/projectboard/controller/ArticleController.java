package com.fastcampus.projectboard.controller;

import com.fastcampus.projectboard.domain.Article;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 */
@RequestMapping("/articles") // 모든 view EndPoint들이 article로 시작된다. - base 매핑주소
@Controller
public class ArticleController {

    @GetMapping // 매핑주소가 베이스 path인 `/articles` 라면 "" 혹은 생략 가능.
    public String articles(ModelMap modelMap) {
        modelMap.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap modelMap) {
        modelMap.addAttribute("article", "article"); // TODO: 구현할 때 여기에 실제 데이터를 넣어줘야 함
        modelMap.addAttribute("articleComments", List.of());
        return "articles/detail";
    }
}
