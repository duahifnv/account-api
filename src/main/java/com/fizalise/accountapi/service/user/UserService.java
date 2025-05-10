package com.fizalise.accountapi.service.user;

import com.fizalise.accountapi.dto.UserDto;
import com.fizalise.accountapi.entity.Account;
import com.fizalise.accountapi.entity.EmailData;
import com.fizalise.accountapi.entity.PhoneData;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.UserAlreadyExistsException;
import com.fizalise.accountapi.exception.UserNotFoundException;
import com.fizalise.accountapi.mapper.UserMapper;
import com.fizalise.accountapi.repository.UserRepository;
import com.fizalise.accountapi.service.AccountService;
import com.fizalise.accountapi.service.AuthService;
import com.fizalise.accountapi.service.data.EmailDataService;
import com.fizalise.accountapi.service.data.PhoneDataService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PhoneDataService phoneDataService;
    private final UserMapper userMapper;
    private final EmailDataService emailDataService;
    private final AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${account.increase.coefficient}")
    private double increaseCoefficient;
    @Value("${account.increase.interval}")
    private Duration increaseInterval;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        return new org.springframework.security.core.userdetails.User(username,
                user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> findAllUsers(String key, String value,
                                   @NotNull Integer pageSize, @NotNull Integer pageNumber, Sort sort) {
        PageRequest pageRequest = getPageRequest(pageSize, pageNumber, sort);
        Page<User> page;
        switch (key) {
            case "dateOfBirth" -> {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate dateOfBirth = LocalDate.parse(value, formatter);
                    page = userRepository.findAllByDateOfBirthAfter(dateOfBirth, pageRequest);
                } catch (DateTimeParseException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат даты");
                }
            }
            case "name" -> page = userRepository.findAllByNameLike(value, pageRequest);
            case "phone" -> {
                User user = findByPhone(value).orElse(null);
                page = new PageImpl<>(List.of(user));
            }
            case "email" -> {
                User user = findByEmail(value).orElse(null);
                page = new PageImpl<>(List.of(user));
            }
            case null -> page = userRepository.findAll(pageRequest);
            default -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестный параметр");
            }
        }
        return page;
    }

    public User findByUsername(String username) {
        return findByEmail(username)
                .or(() -> findByPhone(username))
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public Optional<User> findByIdWithCollections(Long id) {
        return userRepository.findByIdWithCollections(id);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
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
        log.info("Пользователь без данных добавлен в базу: {}", persistedUser);
        User augmentedUser = setUserData(persistedUser, userDto);
        log.info("За пользователем {} закреплены пользовательские данные", augmentedUser.getId());
        return augmentedUser;
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

        Account account = accountService.createAccount(user, userDto.accountDeposit());
        user.setAccount(account);

        return userRepository.save(user);
    }

    public String getUserFirstEmail(User user) {
        return user.getEmails().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Почта не найдена"))
                .getEmail();
    }

    public String getUserFirstPhone(User user) {
        return user.getPhones().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("Телефон не найден"))
                .getPhone();
    }

    @Transactional
    public void addUserPhone(Authentication authentication, String phone) {
        User user = findByUsername(authentication.getName());
        phoneDataService.createUserData(user, phone);
        log.debug("Добавлен телефон для пользователя {}: [{}]", authentication.getName(), phone);
    }

    @Transactional
    public void addUserEmail(Authentication authentication, String email) {
        User user = findByUsername(authentication.getName());
        emailDataService.createUserData(user, email);
        log.debug("Добавлена почта для пользователя {}: [{}]", authentication.getName(), email);
    }


    @Transactional
    public void updateUserPhone(Authentication authentication, String oldPhone, String newPhone) {
        User user = findByUsername(authentication.getName());
        phoneDataService.updateUserData(user, oldPhone, newPhone);
        log.debug("Обновлен телефон пользователя {}: [{}] -> [{}]", authentication.getName(), oldPhone, newPhone);
    }

    @Transactional
    public void updateUserEmail(Authentication authentication, String oldEmail, String newEmail) {
        User user = findByUsername(authentication.getName());
        emailDataService.updateUserData(user, oldEmail, newEmail);
        log.debug("Обновлена почта пользователя {}: [{}] -> [{}]", authentication.getName(), oldEmail, oldEmail);
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    @Transactional
    public void updateAllAccountBalances() {
        List<User> users = findAllUsers();
        for (User user : users) {
            Account account = user.getAccount();
            if (!hasUpdatableState(account)) {
                continue;
            }

            BigDecimal oldBalance = account.getBalance();
            BigDecimal updatedBalance = getUpdatedBalance(account);
            accountService.updateBalance(account, updatedBalance);

            user.setAccount(account);
            log.debug("Обновлен баланс пользователя {}: [{}] -> [{}]",
                    user.getName(), oldBalance, updatedBalance);
            saveUser(user);
        }
    }

    private boolean hasUpdatableState(Account account) {
        return account.getBalance().compareTo(account.getMaxBalance()) <= 0 && (
                ChronoUnit.MILLIS.between(account.getLastBalanceUpdate(), LocalDateTime.now()) >= increaseInterval.toMillis()
        );
    }

    private BigDecimal getUpdatedBalance(Account account) {
        BigDecimal updatedBalance = account.getBalance()
                .multiply(BigDecimal.valueOf(increaseCoefficient));
        return updatedBalance.compareTo(account.getMaxBalance()) <= 0 ?
                updatedBalance : account.getMaxBalance();
    }

    private PageRequest getPageRequest(Integer size, Integer page, Sort sort) {
        return PageRequest.of(page, size, sort);
    }
}
