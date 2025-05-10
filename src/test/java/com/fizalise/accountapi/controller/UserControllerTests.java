package com.fizalise.accountapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizalise.accountapi.config.AuthorizationFilter;
import com.fizalise.accountapi.dto.UserResponseDto;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Mock
    @MockitoBean
    private UserMapper userMapper;
    @Mock
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthorizationFilter authorizationFilter;

    @Test
    void getCurrentUser_shouldReturnCurrentUser() throws Exception {
        String username = "john";
        Authentication auth = mock(Authentication.class);
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name(username)
                .build();
        User user = new User();

        when(userService.findByUsername(username))
                .thenReturn(user);
        when(userMapper.toUserResponseDto(user))
                .thenReturn(userResponseDto);

        mvc.perform(get("/users/me").principal(auth))
                .andExpect(status().isOk());
    }
}