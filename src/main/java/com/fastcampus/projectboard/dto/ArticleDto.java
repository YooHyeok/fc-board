package com.fastcampus.projectboard.dto;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 * record : (private final) 불변 데이터 객체를 쉽게 생성할 수 있도록 하는 새로운 유형의 클래스이다.
 */
public record ArticleDto(
        LocalDateTime createdAt,
        String createdBy,
        String title,
        String content,
        String hashtag
//) implements Serializable { // jackson 직렬화를 사용할것이기 때문에 자바 직렬화를 사용하지 않는다.
) {
    public static ArticleDto of(LocalDateTime createdAt, String createdBy, String title, String content, String hashtag) {
        return new ArticleDto(createdAt, createdBy, title, content, hashtag);
    }
}