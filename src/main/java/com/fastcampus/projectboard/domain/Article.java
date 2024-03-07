package com.fastcampus.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true) // 부모 필드까지 Tostring 적용
@Table(indexes = { /* 빠르게 서칭이 가능하게끔 인덱스 설정 */
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //MySql의 자동증가값은 IDENTITY방식이다.
    private Long id;

    /* setter를 긱 필드레벨에 건 이유는 사용자가 특정필드에 접근한 세팅을 하지 못하게끔 막기 위해 (ex: id와 created의 경우 자동값 부여이므로) */
    @Setter @ManyToOne(optional = false) private UserAccount userAccount; //유저 정보(ID)
    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; // 본문
    @Setter private String hashtag; // 해시태그

    /* 한번만 세팅하기 때문에 final 키워드 사용 가능 양방향 바인딩을 하게되면 강결합 특성때문에 실무에서는 푸는경우가 많다 */
    @ToString.Exclude //circural reference 문제 (순환참조문제)
    @OrderBy("createdAt DESC") //정렬 기준
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    /**
     * Entity 기본생성자 <br/>
     * Hibernate 구현체를 사용하는 기준의 모든 JPA 엔티티는 기본생성자를 가지고 있어야 한다. <br/>
     * protected - 클래스 외부에서 직접 new로 생성하지 못하게끔 막는다.
     */
    protected Article() {}

    /**
     * 생성자 <br/>
     * 도메인과 관련된 정보만 오픈할 수 있게끔 메타데이터 제거하여 생성 <br/>
     * private - New 키워드를 사용하지 않게 하기위해 Factory Method활용
     * @param title
     * @param content
     * @param hashtag
     */
    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    /**
     * Factory Method <br/>
     * 의도를 전달한다. <br/>
     * 도메인 아티클을 생성하고자 할때 이 메소드를 통해 가이드한다. <br/>
     * (컬렉션, stream, Optional에서 생성하는 of와 비슷하다...)
     * @param title
     * @param content
     * @param hashtag
     * @return
     */
    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    /**
     * Accept subclasses as parameter to equals() method 체크한다.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
//        return id.equals(article.id); // 영속성 전 (insert 전 - id부여안됨)일때 null이다. 아래와 같이 null을 체크할 필요가 있다.
        return id != null && id.equals(article.id); // 지금 막 만든, 아직 영속화 되지 않은 엔티티는 모두 동등성 검사를 탈락하게된다.
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
