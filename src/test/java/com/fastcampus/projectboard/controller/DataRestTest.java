package com.fastcampus.projectboard.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest // 컨트롤러의 slice테스트
/**
 * [DataRest에 대한 테스트를 하는 클래스]
 * @WebMVC 테스트는 슬라이스 테스트이다.
 * 컨트롤러 외의 빈들을 로드하지 않는다.
 * 불필요하다고 여겨지는 내용들을 로드하지 않고 컨트롤러와 연관된 내용만 최소한으로 읽어들인다.
 * 데이터 레스트에 오토 컨피규레이션을 읽지 않은것이다. (설정 어렵고 불편함)
 * [인테그레이션] 테스트로 작성하는법 : SpringBootTest, AutoConfigureMockMvc
 *  ㄴ API를 실행한 결과가 리포지토리까지 전부 실행시키므로 Hibernate의 Query를 출력한다.
 *     DB에 영향을 주는 테스트로 클래스 상단에 Transactional Annotation필요
 * 이 테스트는 DB쪽에 모킹(가짜객체)이 되지 않는다
 */
@Disabled("Spring Data REST 통합 테스트는 불필요하므로 제외시킴") //해당 테스트클래스 밑에 있는 모든 유닛 테스트 메소드들은 실행되지 않게 된다.
@DisplayName("Data REST - API 테스트")
@Transactional // 인터그레이션 테스트의 롤백을 위한 어노테이션 선언
@AutoConfigureMockMvc // Mock MVC존재를 알리는 어노테이션
@SpringBootTest // 경량화를 위한 webEnvironment 기본값은 MOCK이므로 따로 지정하지 않는다. (Mock을 사용해야 데이터를 읽어온다.)
public class DataRestTest {
    private final MockMvc mvc;

    public DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNothing_whenRequestingAndArticles_thenReturnsArticlesJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk()) //상태 검사
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json"))); //미디어 타입 검사
//                .andDo(print()); // api를 호출한 결과를 콘솔에 출력한다.
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestingAndArticle_thenReturnsArticlesJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk()) //상태 검사
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json"))); //미디어 타입 검사
//                .andDo(print()); // api를 호출한 결과를 콘솔에 출력한다.
    }

    @DisplayName("[api] 게시글 -> 댓글 리스트 단건 조회")
    @Test
    void givenNothing_whenRequestingAndArticleCommentsFromArticle_thenReturnsArticleCommmentsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articles/1/articleComments"))
                .andExpect(status().isOk()) //상태 검사
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json"))) //미디어 타입 검사
                .andDo(print()); // api를 호출한 결과를 콘솔에 출력한다.
    }

    @DisplayName("[api] 게시글 -> 댓글 리스트 단건 조회")
    @Test
    void givenNothing_whenRequestingAndArticleComments_thenReturnsArticleCommmentsJsonResponse() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk()) //상태 검사
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json"))) //미디어 타입 검사
                .andDo(print()); // api를 호출한 결과를 콘솔에 출력한다.
    }

    @DisplayName("[api] 회원 관련 API 는 일체 제공하지 않는다.")
    @Test
    void givenNothing_whenRequestingUserAccounts_thenThrowsException() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(post("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(put("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(patch("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/userAccounts")).andExpect(status().isNotFound());
        mvc.perform(head("/api/userAccounts")).andExpect(status().isNotFound());
    }
}
