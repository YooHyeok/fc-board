package com.fastcampus.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // springboot에서는 autoconfig에 등록되어있으므로 @EnableWebSecurity 생략 가능
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
//                .authorizeRequests().antMatchers("/**").permitAll()
//                .authorizeHttpRequests().antMatchers("/**").permitAll()
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) //어떤 요청이든 인가 허용
                .formLogin().and()
                .build();
    }
}
