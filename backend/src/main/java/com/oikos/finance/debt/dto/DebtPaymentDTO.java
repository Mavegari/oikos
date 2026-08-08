package com.oikos.finance.debt.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DebtPaymentDTO(
    UUID id,
    BigDecimal amount,
    String date
) {}