package com.hcommerce.heecommerce.auth;

import com.hcommerce.heecommerce.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final String AUTH_TYPE = "Bearer";

    private final JwtUtils jwtUtils;

    @Autowired
    public AuthenticationService(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public String login(int userId) {
        return jwtUtils.encode(userId);
    }

    public TokenPayload parseAuthorization(String authorization) {
        if(authorization == null) {
            throw new RuntimeException(); // Bad Reqeust : 로그인이 필요합니다.
        }

        String accessToken = extractAccessToken(authorization);

        if(accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException(); // Bad Reqeust : 유효하지 않은 토큰입니다.
        }

        try {
            Claims claims = jwtUtils.decode(accessToken);

            Integer userId = claims.get("userId", Integer.class);

            if(userId == null || userId <= 0) {
                throw new RuntimeException(); // Bad Reqeust : 유효하지 않은 회원입니다.
            }

            return new TokenPayload(Integer.valueOf(userId));
        } catch (SignatureException e) {
            throw new RuntimeException(e); // Bad Reqeust : 유효하지 않은 토큰입니다.
        }
    }

    private String extractAccessToken(String authorization) {
        return authorization.substring((AUTH_TYPE+" ").length());
    }
}
