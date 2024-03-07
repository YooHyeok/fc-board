package com.fastcampus.projectboard.dto;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 * record : (private final) 불변 데이터 객체를 쉽게 생성할 수 있도록 하는 새로운 유형의 클래스이다.
 */
public record ArticleUpdateDto(
        String title,
        String content,
        String hashtag
) {
    public static ArticleUpdateDto of(String title, String content, String hashtag) {
        return new ArticleUpdateDto(title, content, hashtag);
    }
}