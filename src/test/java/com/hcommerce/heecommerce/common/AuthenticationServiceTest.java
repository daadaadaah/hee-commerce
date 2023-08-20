package com.hcommerce.heecommerce.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import com.hcommerce.heecommerce.auth.AuthenticationService;
import com.hcommerce.heecommerce.auth.TokenPayload;
import com.hcommerce.heecommerce.common.utils.JwtUtils;
import com.hcommerce.heecommerce.fixture.AuthFixture;
import com.hcommerce.heecommerce.fixture.JwtFixture;
import com.hcommerce.heecommerce.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

@DisplayName("OrderService")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String secret;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("login")
    class Describe_Login {
        @Test
        void It_returns_accessToken() {
            given(jwtUtils.encode(anyInt())).willReturn(JwtFixture.TOKEN);

            String accessToken = authenticationService.login(UserFixture.ID);

            assertThat(accessToken).contains(".");
        }
    }

    @Nested
    @DisplayName("parseAuthorization")
    class Describe_ParseAuthorization {
        @Nested
        @DisplayName("with valid authorization")
        class Context_With_Valid_Authorization {
            @Test
            void It_returns_TokenPayload() {
                given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

                TokenPayload tokenPayload = authenticationService.parseAuthorization(AuthFixture.AUTHORIZATION);

                assertEquals(tokenPayload.getUserId(), UserFixture.ID);
            }
        }

        @Nested
        @DisplayName("with invalid authorization")
        class Context_With_Invalid_Authorization {
            @Test
            void It_returns_TokenPayload() {
                given(jwtUtils.decode(any())).willReturn(JwtFixture.CLAIMS);

                TokenPayload tokenPayload = authenticationService.parseAuthorization(AuthFixture.AUTHORIZATION);

                assertEquals(tokenPayload.getUserId(), UserFixture.ID);
            }
        }
    }
}