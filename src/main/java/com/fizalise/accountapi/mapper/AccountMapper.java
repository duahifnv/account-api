package com.fizalise.accountapi.mapper;

import com.fizalise.accountapi.dto.AccountDto;
import com.fizalise.accountapi.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", imports = Long.class)
public abstract class AccountMapper {
    public abstract AccountDto toAccountDto(Account account);
}
