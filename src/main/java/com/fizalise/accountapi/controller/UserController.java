package com.fizalise.accountapi.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fizalise.accountapi.dto.UserResponseDto;
import com.fizalise.accountapi.dto.Views;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @JsonView(Views.Public.class)
    public List<UserResponseDto> getUsers(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String value,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return userMapper.toUserResponseDtoList(
                userService.findAllUsers(key,
                                value,
                                pageable.getPageSize(),
                                pageable.getPageNumber(),
                                pageable.getSort())
                        .stream().toList()
        );
    }

    @GetMapping("/me")
    @JsonView(Views.Private.class)
    public UserResponseDto getCurrentUser(Authentication authentication) {
        return userMapper.toUserResponseDto(
                userService.findByUsername(authentication.getName())
        );
    }
}
