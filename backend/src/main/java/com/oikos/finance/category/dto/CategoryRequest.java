package com.oikos.finance.category.dto;

import com.oikos.finance.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @NotNull(message = "El tipo es obligatorio (INCOME o EXPENSE)")
        CategoryType type,

        String color
) {
}