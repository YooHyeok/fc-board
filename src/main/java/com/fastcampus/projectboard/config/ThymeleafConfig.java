package com.fastcampus.projectboard.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

/**
 * decoupled template logic 설정
 * 스프맅부트 프로퍼티인 application.yml에서 지원하지 않는다.
 * ex) spring:thymeleaf:decoupled logic 와 같은 설정을 아직 지원을 하지 않아서 이렇게 직접 설정해준다.
 */
@Configuration
public class ThymeleafConfig {

    /**
     * thymeleafTemplateResolver 빈 등록
     * SpringResourceTemplateResolver에 있는 기능을 그대로 가져와서
     * thymeleaf3Properties에 decoupled template logic 기능을 하나 추가해줌
     * @param defaultTemplateResolver
     * @param thymeleaf3Properties : @Bean등록 아래에 static Class를 선언했다.
     * @return SpringResourceTemplateResolver
     */
    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(SpringResourceTemplateResolver defaultTemplateResolver,Thymeleaf3Properties thymeleaf3Properties) {
        /**
         * isDecoupledLogic() : properties에서 해당 프로퍼티 값을 불러들인다.
         * application.yml(properties)
         * spring:
         *   thymeleaf3:
         *     decoupled-logic: true
         * 만약 properties에 해당 값을 생략할경우 thymeleaf3Properties.isDecoupledLogic()를 true로 설정 가능 및 아래 스태틱클래스 제거가능
         */
        defaultTemplateResolver.setUseDecoupledLogic(thymeleaf3Properties.isDecoupledLogic());

        return defaultTemplateResolver;
    }

    /**
     * 프로퍼티 설정파일을 자바 클래스로 매핑
     * @Bean 으로 등록한 위의 로직에서 decoupleLogic을 사용을 설정해준다.
     */
    @RequiredArgsConstructor
    @Getter
    @ConstructorBinding
    @ConfigurationProperties("spring.thymeleaf3") // application.yml에서 spring.thymeleaf3 프로퍼티를 읽어와 자바 클래스로 매핑한다.
    // configuration property를 직접 만든경우 메인클래스에서 반드시 스캔해줘야한다. - @ConfigurationPropertiesScan
    public static class Thymeleaf3Properties {
        /**
         * Use Thymeleaf 3 Decoupled Logic
         */
        private final boolean decoupledLogic; //spring.thymeleaf3 프로퍼티에 decoupledLogic 설정할 수 있게끔 설정 필드 선언 (뚫었다고 보면 됨)
    }

}
