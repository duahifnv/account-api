package com.fizalise.accountapi.controller;

import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) String key,
            @RequestParam(required = false) String value,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return userService.findAllUsers(key, value, pageable.getPageSize(), pageable.getPageNumber())
                .stream().toList();
    }
}
