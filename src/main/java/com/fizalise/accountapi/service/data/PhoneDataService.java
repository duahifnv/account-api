package com.fizalise.accountapi.service.data;

import com.fizalise.accountapi.entity.PhoneData;
import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.exception.ForbiddenException;
import com.fizalise.accountapi.exception.ResourceNotFoundException;
import com.fizalise.accountapi.repository.PhoneDataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PhoneDataService extends DataService<PhoneData, PhoneDataRepository> {
    public PhoneDataService(PhoneDataRepository repository) {
        super(repository);
    }
    @Transactional
    @Override
    public PhoneData createUserData(User user, String phone) {
        PhoneData phoneData = PhoneData.builder()
                .user(user)
                .phone(phone)
                .build();
        return saveData(phoneData);
    }
    @Transactional
    public void updateUserData(User user, String oldPhone, String newPhone) {
        PhoneData phoneData = repository.findByPhone(oldPhone)
                .orElseThrow(() -> new ResourceNotFoundException("Телефон не найден"));
        if (!user.getPhones().contains(phoneData)) {
            throw new ForbiddenException();
        }
        if (repository.existsByPhone(newPhone)) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Телефон уже существует");
        }
        phoneData.setPhone(newPhone);
        saveData(phoneData);
    }
    @Transactional
    @Override
    public void deleteUserData(User user, String phone) {
        PhoneData phoneData = repository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Телефон не найден"));
        if (!user.getPhones().contains(phoneData)) {
            throw new ForbiddenException();
        }
        saveData(phoneData);
    }
}
