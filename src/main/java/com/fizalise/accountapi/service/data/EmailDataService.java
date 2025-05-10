package com.fizalise.accountapi.service.data;

import com.fizalise.accountapi.entity.EmailData;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.ForbiddenException;
import com.fizalise.accountapi.exception.ResourceNotFoundException;
import com.fizalise.accountapi.repository.EmailDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailDataService extends DataService<EmailData, EmailDataRepository> {
    public EmailDataService(EmailDataRepository repository) {
        super(repository);
    }

    @Transactional
    @Override
    public EmailData createUserData(User user, String email) {
        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Почта уже существует");
        }
        EmailData emailData = EmailData.builder()
                .user(user)
                .email(email)
                .build();
        return saveData(emailData);
    }

    @Transactional
    @Override
    public void updateUserData(User user, String oldEmail, String newEmail) {
        EmailData emailData = repository.findByEmail(oldEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Почта не найдена"));
        if (!user.getEmails().contains(emailData)) {
            throw new ForbiddenException();
        }
        if (repository.existsByEmail(newEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Почта уже существует");
        }
        emailData.setEmail(newEmail);
        saveData(emailData);
    }

    @Transactional
    @Override
    public void deleteUserData(User user, String email) {
        EmailData emailData = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Почта не найдена"));
        if (!user.getEmails().contains(emailData)) {
            throw new ForbiddenException();
        }
        saveData(emailData);
    }
}
