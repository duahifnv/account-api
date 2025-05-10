package com.fizalise.accountapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @PositiveOrZero
    @Column(name = "max_balance", nullable = false,
            precision = 19, scale = 4)
    private BigDecimal maxBalance;

    @Column(name = "last_balance_update", nullable = false)
    private LocalDateTime lastBalanceUpdate;
}