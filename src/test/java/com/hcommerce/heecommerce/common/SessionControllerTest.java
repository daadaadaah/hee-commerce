package com.hcommerce.heecommerce.common;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hcommerce.heecommerce.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // TODO : @SpyBean 과의 차이
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        given(authenticationService.login(any())).willReturn("a.b.c");
    }

    @Test
    void login() throws Exception {
        mockMvc.perform(post("/session"))
            .andExpect(status().isCreated())
            .andExpect(content().string(containsString(".")));
    }
}
