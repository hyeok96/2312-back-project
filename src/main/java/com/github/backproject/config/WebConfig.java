package com.github.backproject.config;

import com.github.backproject.config.security.JwtProvider;
import com.github.backproject.respository.userPrincipal.UserPrincipalRepository;
import com.github.backproject.service.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userAuthArgumentResolver());
    }

    @Bean
    public UserAuthArgumentResolver userAuthArgumentResolver () {
        return new UserAuthArgumentResolver(jwtProvider);
    }
}
