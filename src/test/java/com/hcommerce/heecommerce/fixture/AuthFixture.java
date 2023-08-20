package com.hcommerce.heecommerce.fixture;

import com.hcommerce.heecommerce.auth.TokenPayload;

public class AuthFixture {

    public static final String AUTH_TYPE = "Bearer";

    public static final TokenPayload TOKEN_PAYLOAD = new TokenPayload(UserFixture.ID);

    public static final String AUTHORIZATION = AUTH_TYPE+" "+JwtFixture.TOKEN;
}
