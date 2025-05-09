package com.fizalise.accountapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.fizalise.accountapi.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from EmailData ed join User u on ed.user = u where ed.email = ?1")
    Optional<User> findByEmail(String email);
    @Query("select u from PhoneData pd join User u on pd.user = u where pd.phone = ?1")
    Optional<User> findByPhone(String phone);
}
