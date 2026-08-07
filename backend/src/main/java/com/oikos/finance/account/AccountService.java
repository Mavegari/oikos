package com.oikos.finance.account;

import com.oikos.finance.account.dto.AccountRequest;
import com.oikos.finance.account.dto.AccountResponse;
import com.oikos.finance.transaction.TransactionRepository;
import com.oikos.finance.transaction.TransactionType;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // CREAR
    public AccountResponse create(AccountRequest request, User user) {
    if (accountRepository.existsByUserAndName(user, request.name())) {
        throw new IllegalArgumentException("Ya tienes una cuenta con ese nombre");
    }

    Account account = new Account(user, request.name(), request.type(), request.initialBalance());
    Account saved = accountRepository.save(account);
    return toResponse(saved);
    }

    // LISTAR
    public List<AccountResponse> findAll(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // OBTENER una
    public AccountResponse findById(UUID id, User user) {
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        return toResponse(account);
    }

    // ACTUALIZAR
    public AccountResponse update(UUID id, AccountRequest request, User user) {
    Account account = accountRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

    account.setName(request.name());
    account.setType(request.type());
    account.setInitialBalance(request.initialBalance());

    Account updated = accountRepository.save(account);
    return toResponse(updated);
    }

    // ELIMINAR
    public void delete(UUID id, User user) {
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        accountRepository.delete(account);
    }

    // Conversión entidad → DTO, con cálculo de balance
    private AccountResponse toResponse(Account account) {
    BigDecimal balance = calculateBalance(account);
    return new AccountResponse(
            account.getId(),
            account.getName(),
            account.getType(),
            account.getInitialBalance(),
            balance
    );
    }

    // Cálculo del saldo — de momento cero, se completará con Transaction
    private BigDecimal calculateBalance(Account account) {
    BigDecimal ingresos = transactionRepository
            .sumAmountByAccountAndType(account, TransactionType.INCOME);
    BigDecimal gastos = transactionRepository
            .sumAmountByAccountAndType(account, TransactionType.EXPENSE);
    BigDecimal transactionBalance = ingresos.subtract(gastos);
    return account.getInitialBalance().add(transactionBalance);
    }
}