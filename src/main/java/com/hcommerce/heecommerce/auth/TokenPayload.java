package com.hcommerce.heecommerce.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenPayload {

    private final int userId;

    public TokenPayload(int userId) {
        this.userId = userId;
    }
}
