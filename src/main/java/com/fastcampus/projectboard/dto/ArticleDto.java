package com.fastcampus.projectboard.dto;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.fastcampus.projectboard.domain.Article}
 * record : (private final) 불변 데이터 객체를 쉽게 생성할 수 있도록 하는 새로운 유형의 클래스이다.
 */
public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
//) implements Serializable { // jackson 직렬화를 사용할것이기 때문에 자바 직렬화를 사용하지 않는다.
) {

    /**
     * Article 실제 저장용 of
     * @param userAccountDto
     * @param title
     * @param content
     * @param hashtag
     * @return
     */
    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, String hashtag) {
        return new ArticleDto(null, userAccountDto, title, content, hashtag, null, null, null, null);
    }

    /**
     * Article 저장 테스트용 of
     * @param id
     * @param userAccountDto
     * @param title
     * @param content
     * @param hashtag
     * @param createdAt
     * @param createdBy
     * @param modifiedAt
     * @param modifiedBy
     * @return
     */
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, String hashtag, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity(UserAccount userAccount) {
        return Article.of(
                userAccount,
                title,
                content,
                hashtag
        );
    }
}