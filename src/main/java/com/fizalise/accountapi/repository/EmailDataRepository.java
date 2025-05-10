package com.fizalise.accountapi.repository;

import com.fizalise.accountapi.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmail(String email);

    Optional<EmailData> findByEmail(String email);
}
