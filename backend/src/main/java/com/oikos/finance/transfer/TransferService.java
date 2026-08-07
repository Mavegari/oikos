package com.oikos.finance.transfer;

import com.oikos.finance.account.Account;
import com.oikos.finance.account.AccountRepository;
import com.oikos.finance.transfer.dto.TransferRequest;
import com.oikos.finance.transfer.dto.TransferResponse;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {
    
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    
    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }
    
    @Transactional
    public TransferResponse create(TransferRequest request, User user) {
        // Validar que source != target
        if (request.sourceAccountId().equals(request.targetAccountId())) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma");
        }
        
        // Obtener y validar cuenta origen
        Account sourceAccount = accountRepository.findByIdAndUser(request.sourceAccountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));
        
        // Obtener y validar cuenta destino
        Account targetAccount = accountRepository.findByIdAndUser(request.targetAccountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));
        
        // Crear transferencia
        Transfer transfer = new Transfer(
                user,
                sourceAccount,
                targetAccount,
                request.amount(),
                request.date(),
                request.note()
        );
        
        Transfer saved = transferRepository.save(transfer);
        return toResponse(saved);
    }
    
    public List<TransferResponse> findAll(User user) {
        return transferRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    public TransferResponse findById(UUID id, User user) {
        Transfer transfer = transferRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));
        return toResponse(transfer);
    }
    
    @Transactional
    public TransferResponse update(UUID id, TransferRequest request, User user) {
        Transfer transfer = transferRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));
        
        // Validar que source != target
        if (request.sourceAccountId().equals(request.targetAccountId())) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma");
        }
        
        // Validar y obtener cuentas
        Account sourceAccount = accountRepository.findByIdAndUser(request.sourceAccountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada"));
        
        Account targetAccount = accountRepository.findByIdAndUser(request.targetAccountId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada"));
        
        // Actualizar
        transfer.setSourceAccount(sourceAccount);
        transfer.setTargetAccount(targetAccount);
        transfer.setAmount(request.amount());
        transfer.setDate(request.date());
        transfer.setNote(request.note());
        
        Transfer updated = transferRepository.save(transfer);
        return toResponse(updated);
    }
    
    @Transactional
    public void delete(UUID id, User user) {
        Transfer transfer = transferRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Transferencia no encontrada"));
        transferRepository.delete(transfer);
    }
    
    private TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSourceAccount().getId(),
                transfer.getSourceAccount().getName(),
                transfer.getTargetAccount().getId(),
                transfer.getTargetAccount().getName(),
                transfer.getAmount(),
                transfer.getDate(),
                transfer.getNote(),
                transfer.getCreatedAt()
        );
    }
}