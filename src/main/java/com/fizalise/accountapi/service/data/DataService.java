package com.fizalise.accountapi.service.data;

import com.fizalise.accountapi.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class DataService<T, R extends JpaRepository<T, Long>> {
    protected final R repository;

    @Transactional
    public T saveData(T data) {
        return repository.save(data);
    }

    public abstract T createUserData(User user, String dataValue);

    public abstract void updateUserData(User user, String oldDataValue, String newDataValue);

    public abstract void deleteUserData(User user, String dataValue);
}
