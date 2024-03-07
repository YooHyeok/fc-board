package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true) // 부모 필드까지 Tostring 적용
@Table(indexes = { /* 빠르게 서칭이 가능하게끔 인덱스 설정 */
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class) // Auditing동작 어노테이션 필수다!
@Entity
public class ArticleComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //MySql의 자동증가값은 IDENTITY방식이다.
    private Long id;

    @Setter @ManyToOne(optional = false) private UserAccount userAccount; //유저 정보(ID)

    @Setter @ManyToOne(optional = false) // optional하지않는다.(필수값 - innerJoin)
    private Article article; // 게시글 (ID)
    @Setter @Column(nullable = false, length = 500) private String content; // 본문

    protected ArticleComment() {}

    private ArticleComment(Article article, UserAccount userAccount, String content) {
        this.article = article;
        this.userAccount = userAccount;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content) {
        return new ArticleComment(article, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
