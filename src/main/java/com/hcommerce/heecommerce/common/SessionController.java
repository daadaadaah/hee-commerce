package com.hcommerce.heecommerce.common;

import com.hcommerce.heecommerce.auth.AuthenticationService;
import com.hcommerce.heecommerce.common.dto.SessionResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {

    private AuthenticationService authenticationService;

    public SessionController(
        AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseData login(@RequestBody int userId) { // TODO : 임시로 일단 userId 만
        String accessToken = authenticationService.login(userId);

        return SessionResponseData.builder()
                    .accessToken(accessToken)
                    .build();
    }
}
