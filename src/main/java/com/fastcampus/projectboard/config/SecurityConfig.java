package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // springboot에서는 autoconfig에 등록되어있으므로 @EnableWebSecurity 생략 가능
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
//                .authorizeRequests().antMatchers("/**").permitAll()
//                .authorizeHttpRequests().antMatchers("/**").permitAll()
                .authorizeHttpRequests(auth -> auth
//                        .antMatchers("/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(// antMatcher와 완벽하게 호환되며, 스프링 기반 패턴 매칭에 들어가는 룰이 더 추가됨.
                                HttpMethod.GET, // POST - 추가/수정/삭제에 대한 권한체킹을 한다.
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll() // 위 mvcMatcher에 대한 전체 허용
                        .anyRequest().authenticated() // 그 외 나머지 어떠한 요청에도 인증이 되어야만 하도록 설정
                ) //어떤 요청이든 인가 허용
                .formLogin().and()
                .logout()
                        .logoutSuccessUrl("/")
                        .and()
                .build();
    }

    /**
     * 정적페이지를 무시해도 문제가 없겠으나, 해당 경로에대해 시큐리티에 관련된 모든 서비스를 사용하지 않으므로 CSRF등의 보안 공격에 대한 방어기능이나
     * 다른 어떤 보안 공격에 대해 취약해 질 수 밖에 없으므로 권장되지 않는다.
     * WebSecurity가 아닌 HttpSecurty에서 permitAll 하라고 경고 로그가 뜬다.
     * 해당 위치에 선언하면 함께 보안기능을 통해 관리될 수 있기 때문이다.
     *
     * 이전 WebSecurityConfigurer의 configure(WebSecurity web) 에서 적용하던 보안필터 기능
     * static resource, css - js 등은 서버사이드에서 처리가 일어나는 부분이 아니기 때문에
     * SpringSecurity 검사에서 제외한다.
     * antMatchers()로 하나하나 지정하는 대신, 다양한 정적 리소스에 경로에 대해 보편적으로 미리 정해둔
     * requestMatchers()와 PageRequest.toStaticResources().atCommonLocations()를 활용한다.
     * @return
     */
/*    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().antMatchers("/css/**", "/js/**");
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }*/

    /**
     * 보안, 인증, 사용자정보를 가져온다
     * UserDetailsService의 loadUseerByUsername()의 람다식을 반환한다.
     */
    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository.findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다 - username: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder(); // 팩토리로부터 설정을 위임하여 가져온다.
    }

}
