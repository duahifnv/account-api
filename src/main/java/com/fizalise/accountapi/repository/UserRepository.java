package com.fizalise.accountapi.repository;

import com.fizalise.accountapi.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from EmailData ed join User u on ed.user = u where ed.email = ?1")
    Optional<User> findByEmail(String email);

    @Query("select u from PhoneData pd join User u on pd.user = u where pd.phone = ?1")
    Optional<User> findByPhone(String phone);

    Page<User> findAllByDateOfBirthAfter(LocalDate dateOfBirthAfter, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.name LIKE CONCAT(:name, '%')")
    Page<User> findByNameStartingWith(@Param("name") String name, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.emails LEFT JOIN FETCH u.phones WHERE u.id = :id")
    Optional<User> findByIdWithCollections(@Param("id") Long id);
}
