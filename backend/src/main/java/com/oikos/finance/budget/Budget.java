package com.oikos.finance.budget;

import com.oikos.finance.category.Category;
import com.oikos.finance.user.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "budgets",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_budget_user_category_month_year",
        columnNames = {"user_id", "category_id", "month", "year"}
    )
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private Boolean recurring; // default FALSE

    protected Budget() {
    }

    public Budget(User user, Category category, BigDecimal limitAmount, int month, int year, Boolean recurring) {
    this.user = user;
    this.category = category;
    this.limitAmount = limitAmount;
    this.month = month;
    this.year = year;
    this.recurring = recurring != null ? recurring : false;
    }

    // Getters y setters
    public UUID getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Boolean getRecurring() {
    return recurring;
    }

    public void setRecurring(Boolean recurring) {
    this.recurring = recurring;
    }
}