package com.oikos.finance.transfer;

import com.oikos.finance.account.Account;
import com.oikos.finance.user.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transfers")
public class Transfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_account_id", nullable = false)
    private Account targetAccount;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private LocalDate date;
    
    private String note;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Constructores
    public Transfer() {
        this.createdAt = Instant.now();
    }
    
    public Transfer(User user, Account sourceAccount, Account targetAccount, BigDecimal amount, LocalDate date, String note) {
        this.user = user;
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.createdAt = Instant.now();
    }
    
    // Getters y Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Account getSourceAccount() {
        return sourceAccount;
    }
    
    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }
    
    public Account getTargetAccount() {
        return targetAccount;
    }
    
    public void setTargetAccount(Account targetAccount) {
        this.targetAccount = targetAccount;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
}