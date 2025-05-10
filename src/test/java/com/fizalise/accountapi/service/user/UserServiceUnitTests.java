package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.EmailData;
import com.fizalise.accountapi.entity.PhoneData;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.service.data.EmailDataService;
import com.fizalise.accountapi.service.data.PhoneDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PhoneDataService phoneDataService;
    @Mock
    private EmailDataService emailDataService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .email("john@mail.com")
                .phone("79991234567")
                .accountDeposit(BigDecimal.TEN)
                .build();
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .password("password")
                .build();
        PhoneData phoneData = PhoneData.builder().user(user).phone("79991234567").build();
        EmailData emailData = EmailData.builder().user(user).email("john@mail.com").build();

        when(userService.findByEmail("john@mail.com")).thenReturn(Optional.empty());
        when(userService.findByPhone("79991234567")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(phoneDataService.createUserData(user, "79991234567")).thenReturn(phoneData);
        when(emailDataService.createUserData(user, "john@mail.com")).thenReturn(emailData);
        when(userMapper.toUser(userDto)).thenReturn(user);

        User createdUser = userService.createUser(userDto);
        assertEquals(createdUser.getPhones(), Set.of(phoneData));
        assertEquals(createdUser.getEmails(), Set.of(emailData));
    }
}