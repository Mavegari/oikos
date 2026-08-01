package com.oikos.finance.category.dto;

import com.oikos.finance.category.CategoryType;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        CategoryType type,
        String color
) {
}