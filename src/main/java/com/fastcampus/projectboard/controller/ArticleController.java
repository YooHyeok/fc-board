package com.fastcampus.projectboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * /articles/{article-id}
 * /articles/search
 * /articles/search-hashtag
 */
@RequestMapping("/articles") // 모든 view EndPoint들이 article로 시작된다. - base 매핑주소
@Controller
public class ArticleController {
}
