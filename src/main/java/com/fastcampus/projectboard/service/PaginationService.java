package com.fastcampus.projectboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5; // 상태값: Pagenation Bar의 길이 (디자인적 요소)

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0); // 중앙값을 찾기위한 계산식(음수면 0) - 전체길이의 절반만큼 빼면 첫번째 값을 알 수 있다.
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages); // 시작값 + Bar길이시 끝번호가 계산된다.
        return IntStream.range(startNumber,endNumber).boxed().toList(); //[startNumber , startNumber+1 ... , E]
    }
    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
