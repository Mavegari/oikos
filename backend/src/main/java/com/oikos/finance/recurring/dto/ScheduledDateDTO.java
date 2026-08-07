package com.oikos.finance.recurring.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ScheduledDateDTO(
    UUID id,
    LocalDate scheduledDate,
    Boolean executed,
    LocalDate executedDate
) {}