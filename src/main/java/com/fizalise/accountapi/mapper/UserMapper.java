package com.fizalise.accountapi.mapper;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring", imports = Long.class)
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", qualifiedByName = "getEncodedPassword")
    public abstract User toUser(UserDto requestUser);

    @Named("getEncodedPassword")
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
