package com.github.backproject.config;

import com.github.backproject.anotation.JwtAuthorization;
import com.github.backproject.config.security.JwtProvider;
import com.github.backproject.web.dto.auth.AuthInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class UserAuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        String token = webRequest.getHeader("X-AUTH-TOKEN");


//        String token = webRequest.getNativeRequest(HttpServletRequest.class).getHeader("X-AUTH-TOKEN");
        if(token == null && jwtProvider.validateToken(token)) {
            throw new RuntimeException("UnauthorizedException");
        }

        Map<String, Integer> decodeToken = jwtProvider.decode(token);
        Integer userId = decodeToken.get("userId");

        if (userId == null) {
            throw new RuntimeException("UnauthorizedException");
        }

        return AuthInfo.of(userId);
    }
}
