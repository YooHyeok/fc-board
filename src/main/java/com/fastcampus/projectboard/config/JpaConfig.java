package com.fastcampus.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    /**
     * Auditing의 createdBy등에 사람 이름 정보를 넣어주기 위한 설정
     * @return
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("UHyeok"); // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때, 수정하자
    }
}
