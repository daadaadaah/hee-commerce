package com.hcommerce.heecommerce.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hcommerce.heecommerce.fixture.JwtFixture;
import com.hcommerce.heecommerce.fixture.UserFixture;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(JwtFixture.SECRET);
    }

    @Test
    void encode() {
        String token = jwtUtils.encode(UserFixture.ID);

        assertThat(token).isEqualTo(JwtFixture.TOKEN);
    }

    @Test
    void decodeWithValidToken() {
        Claims claims = jwtUtils.decode(JwtFixture.TOKEN);

        assertThat(claims.get("userId", Integer.class)).isEqualTo(UserFixture.ID);
    }

    @Test
    void decodeWithInvalidToken() {
        assertThatThrownBy(() -> jwtUtils.decode(JwtFixture.INVALID_TOKEN))
            .isInstanceOf(SignatureException.class);
    }
}
