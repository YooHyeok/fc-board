package com.fastcampus.projectboard.controller;


import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.request.ArticleCommentRequest;
import com.fastcampus.projectboard.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ArticleCommentController {
    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        // TODO: 인증 정보를 넣어줘야 한다.
        articleCommentService.saveArticleComment(
                articleCommentRequest.toDto(
                        UserAccountDto.of("yooHyeok", "123qwe", "webdevyoo@mail.com", null, null)
                )
        );
        return "redirect:/articles/" + articleCommentRequest.articleId();

    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable long commentId, long articleId) {
        articleCommentService.deleteArticleComment(commentId);
        return "redirect:/articles/" + articleId;
    }

}
