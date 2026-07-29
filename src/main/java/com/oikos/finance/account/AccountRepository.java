package com.oikos.finance.account;

import com.oikos.finance.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUser(User user);

    Optional<Account> findByIdAndUser(UUID id, User user);

    boolean existsByUserAndName(User user, String name);
}