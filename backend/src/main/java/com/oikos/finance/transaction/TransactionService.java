package com.oikos.finance.transaction;

import com.oikos.finance.account.Account;
import com.oikos.finance.account.AccountRepository;
import com.oikos.finance.category.Category;
import com.oikos.finance.category.CategoryRepository;
import com.oikos.finance.transaction.dto.TransactionRequest;
import com.oikos.finance.transaction.dto.TransactionResponse;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREAR
    public TransactionResponse create(TransactionRequest request, User user) {
        // Verificar que la cuenta es del usuario
        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));

        // Verificar que la categoría es del usuario
        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Transaction transaction = new Transaction(
                user, account, category,
                request.amount(), request.type(),
                request.date(), request.note()
        );
        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    // LISTAR
    public List<TransactionResponse> findAll(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // OBTENER una
    public TransactionResponse findById(UUID id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        return toResponse(transaction);
    }

    // ACTUALIZAR
    public TransactionResponse update(UUID id, TransactionRequest request, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));

        // Re-verificar cuenta y categoría (por si cambian y para asegurar propiedad)
        Account account = accountRepository.findByIdAndUser(request.accountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada"));
        Category category = categoryRepository.findByIdAndUser(request.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(request.amount());
        transaction.setType(request.type());
        transaction.setDate(request.date());
        transaction.setNote(request.note());

        Transaction updated = transactionRepository.save(transaction);
        return toResponse(updated);
    }

    // ELIMINAR
    public void delete(UUID id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        transactionRepository.delete(transaction);
    }

    // Conversión entidad → DTO (con nombres de cuenta y categoría)
    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAccount().getId(),
                t.getAccount().getName(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getAmount(),
                t.getType(),
                t.getDate(),
                t.getNote()
        );
    }
}