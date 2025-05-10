package com.fizalise.accountapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizalise.accountapi.config.AuthorizationFilter;
import com.fizalise.accountapi.dto.AuthDto;
import com.fizalise.accountapi.dto.JwtDto;
import com.fizalise.accountapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Mock
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private AuthorizationFilter authorizationFilter;

    @Test
    void authorizeUser_shouldReturnJwt() throws Exception {
        JwtDto jwtDto = new JwtDto("token");
        when(authService.authenticateUser(any(AuthDto.class)))
                .thenReturn(jwtDto);

        AuthDto authDto = new AuthDto("john", "password");
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(authDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));

        verify(authService).authenticateUser(any(AuthDto.class));
    }

    @Test
    void whenUsernameTooShort_thenReturns400() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"abc\",\"password\":\"validPass\"}")) // 3 символа
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPasswordTooShort_thenReturns400() throws Exception {
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"validUser\",\"password\":\"12345\"}")) // 5 символов
                .andExpect(status().isBadRequest());
    }
}