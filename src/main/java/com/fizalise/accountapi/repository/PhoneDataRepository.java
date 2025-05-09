package com.fizalise.accountapi.repository;

import com.fizalise.accountapi.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {
    boolean existsByPhone(String phone);
    Optional<PhoneData> findByPhone(String phone);
}
