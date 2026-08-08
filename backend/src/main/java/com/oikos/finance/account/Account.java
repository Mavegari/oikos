package com.oikos.finance.account;

import com.oikos.finance.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private BigDecimal initialBalance;

    protected Account() {
    }

    public Account(User user, String name, AccountType type, BigDecimal initialBalance) {
    this.user = user;
    this.name = name;
    this.type = type;
    this.initialBalance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
    }

    // Getters y setters
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getInitialBalance() {
    return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
    this.initialBalance = initialBalance;
    }
}