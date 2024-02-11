package com.github.backproject.config.security;

import com.github.backproject.respository.user.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final String secreteKey = Base64.getEncoder().encodeToString("super-coding".getBytes());

    private long tokenVailidMillisecond = 1000L * 60 * 60;

    private final UserDetailsService userDetailsService;

    public String resolveToken(HttpServletRequest request) {
        return  request.getHeader("X-AUTH-TOKEN");
    }

    public String createToken(UserEntity userEntity, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userEntity.getUserId().toString());
        claims.put("email", userEntity.getEmail());
        claims.put("roles", roles);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenVailidMillisecond))
                .signWith(SignatureAlgorithm.HS256, secreteKey)
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secreteKey).parseClaimsJws(jwtToken).getBody();
            Date now = new Date();
            return !claims.getExpiration().before(now);
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String jwtToken) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserEmail(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUserEmail(String jwtToken) {
        return (String) Jwts.parser().setSigningKey(secreteKey).parseClaimsJws(jwtToken).getBody().get("email");
    }

    public Map<String, Integer> decode(String jwtToken) {
        String userId = Jwts.parser().setSigningKey(secreteKey).parseClaimsJws(jwtToken).getBody().getSubject();
        return Map.of("userId", Integer.parseInt(userId));
    }
}
