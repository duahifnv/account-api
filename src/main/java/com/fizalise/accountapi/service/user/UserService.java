package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.EmailData;
import com.fizalise.accountapi.entity.PhoneData;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.UserAlreadyExistsException;
import com.fizalise.accountapi.exception.UserNotFoundException;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.service.data.EmailDataService;
import com.fizalise.accountapi.service.data.PhoneDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис пользователей")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PhoneDataService phoneDataService;
    private final UserMapper userMapper;
    private final EmailDataService emailDataService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public Page<User> findAllUsers(Integer size, Integer page) {
        return userRepository.findAll(getPageRequest(size, page));
    }

    public User findByUsername(String username) {
        return findByEmail(username)
                .or(() -> findByPhone(username))
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByEmail(phone);
    }

    @Transactional
    public User createUser(UserDto userDto) {
        if (findByEmail(userDto.email()).isPresent()) {
            throw new UserAlreadyExistsException(userDto.email());
        }
        if (findByPhone(userDto.phone()).isPresent()) {
            throw new UserAlreadyExistsException(userDto.phone());
        }
        User persistedUser = userRepository.save(userMapper.toUser(userDto));
        return setUserData(persistedUser, userDto);
    }

    @Transactional
    public User setUserData(User user, UserDto userDto) {
        Set<PhoneData> phones = new HashSet<>();
        Set<EmailData> emails = new HashSet<>();

        PhoneData phoneData = phoneDataService.createUserData(user, userDto.phone());
        phones.add(phoneData);
        user.setPhones(phones);

        EmailData emailData = emailDataService.createUserData(user, userDto.email());
        emails.add(emailData);
        user.setEmails(emails);

        return userRepository.save(user);
    }

    @Transactional
    public void updateUserPhone(Principal principal, String oldPhone, String newPhone) {
        User user = findByUsername(principal.getName());
        phoneDataService.updateUserData(user, oldPhone, newPhone);
    }

    private PageRequest getPageRequest(Integer size, Integer page) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
    }
}
