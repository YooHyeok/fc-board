package com.fastcampus.projectboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class) //스프링 부트 테스트의 무게가 많이 줄어들도록 설정
class PaginationServiceTest {

//    private final PaginationService sut = new PaginationService();

    private final PaginationService sut;

    PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.sut = paginationService;
    }


    /**
     * @ParameterizedTest : JUnit 고급 테스트기법 파라미터 테스트로 @Test 어노테이션 대신 사용. <br/>
     * 여러번 연속적으로 주입하여 동일한 대상 메소드를 여러번 테스트 하며 입출력값을 볼수있는 기능
     * @MethodSource : 파라미터 입력값 주입 어노테이션 (메소드 주입 방식)
     * @param currentPageNumber : 현제 페이지
     * @param totalpages : 전체 페이지 수
     * @param expected : 검증하고싶은 페이징 바 리스트 값
     * @throws Exception
     */
    @ParameterizedTest(name= "[{index}] 현재페이지: {0}, 총페이지: {1} => {2}")
    @MethodSource
    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면 페이징 바 리스트를 만들어준다. (1, 2, 3, ...)")
    public void givenCurrentPageNumberAndTotalBages_whenCalculating_thenReturnsPaginationBarNumbers (int currentPageNumber, int totalpages, List<Integer> expected) throws Exception {
        //given

        //when
        List<Integer> actual = sut.getPaginationBarNumbers(currentPageNumber, totalpages);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    /**
     * ParameterizedTest의 입출력값을 주입하는 메소드
     * static메소드로 Test메소드의 메소드명과 메소드명이 일치해야한다.
     * @return
     */
    static Stream<Arguments>  givenCurrentPageNumberAndTotalBages_whenCalculating_thenReturnsPaginationBarNumbers() {
        return Stream.of(//123개의 게시글이므로 10개(12) + 3(1) = 13
                arguments(0, 13, List.of(0, 1, 2, 3, 4)),
                arguments(1, 13, List.of(0, 1, 2, 3, 4)),
                arguments(2, 13, List.of(0, 1, 2, 3, 4)),
                arguments(3, 13, List.of(1, 2, 3, 4, 5)),
                arguments(4, 13, List.of(2, 3, 4, 5, 6)),
                arguments(5, 13, List.of(3, 4, 5, 6, 7)),
                arguments(6, 13, List.of(4, 5, 6, 7, 8)),
                arguments(10, 13, List.of(8, 9, 10, 11, 12)),
                arguments(11, 13, List.of(9, 10, 11, 12)),
                arguments(12, 13, List.of(10, 11, 12))
        );
    }

    /**
     * 현재 페이지네이션 길이가 5라는 점을 테스트코드를 통해 드러내기위한 테스트코드 
     * (해당 코드를 통해 협업자가 길이를 간접적으로 파악하게됨)
     * Spec에 명세를 바로 코드에 드러내기 위한 목적
     */
    @Test
    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이를 알려준다.") //
    void givenNothing_whenCalling_thenReturnsCurrentBarLength() {
        // given

        // when
        int barlength = sut.currentBarLength();
        //then
        assertThat(barlength).isEqualTo(5);
    }
}