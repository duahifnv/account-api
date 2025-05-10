package com.fizalise.accountapi.mapper;

import com.fizalise.accountapi.dto.AccountDto;
import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.dto.UserResponseDto;
import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.EmailData;
import com.fizalise.accountapi.entity.PhoneData;
import com.fizalise.accountapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = Long.class)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;

    @Mapping(target = "password", qualifiedByName = "getEncodedPassword")
    public abstract User toUser(UserDto userDto);

    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "dd.MM.yyyy")
    @Mapping(target = "emails", expression = "java(mapEmails(user.getEmails()))")
    @Mapping(target = "phones", expression = "java(mapPhones(user.getPhones()))")
    @Mapping(target = "accountId", expression = "java(getAccountId(user.getAccount()))")
    @Mapping(target = "accountInfo", expression = "java(toAccountDto(user.getAccount()))")
    public abstract UserResponseDto toUserResponseDto(User user);

    public abstract List<UserResponseDto> toUserResponseDtoList(List<User> users);

    @Named("getEncodedPassword")
    protected String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    protected List<String> mapEmails(Set<EmailData> emails) {
        if (emails == null) return Collections.emptyList();
        return emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toList());
    }

    protected List<String> mapPhones(Set<PhoneData> phones) {
        if (phones == null) return Collections.emptyList();
        return phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toList());
    }

    protected Long getAccountId(Account account) {
        return account != null ? account.getId() : null;
    }

    protected AccountDto toAccountDto(Account account) {
        return accountMapper.toAccountDto(account);
    }
}
