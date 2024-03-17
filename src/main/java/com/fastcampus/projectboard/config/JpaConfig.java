package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    /**
     * Auditing의 createdBy등에 사람 이름 정보를 넣어주기 위한 설정
     * 시큐리티 정보를 모두 들고있는 SecurityContextHolder를 통해 SecurityContext를 반환받는다
     * @return
     */

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication) //Authentication 객체 반환
                .filter(Authentication::isAuthenticated) // 인증된 객체만 필터링
                .map(Authentication::getPrincipal) //로그인 정보인 pricipal 객체 반환
                .map(BoardPrincipal.class::cast) // BoardPrincipal(extends UserDetails)로 타입케스팅
                .map(BoardPrincipal::getUsername);
//        return () -> Optional.of("UHyeok"); // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정하자
    }
}
