package com.oikos.finance.category;

import com.oikos.finance.category.dto.CategoryRequest;
import com.oikos.finance.category.dto.CategoryResponse;
import com.oikos.finance.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREAR
    public CategoryResponse create(CategoryRequest request, User user) {
        // Evitar nombres duplicados para el mismo usuario
        if (categoryRepository.existsByUserAndName(user, request.name())) {
            throw new IllegalArgumentException("Ya tienes una categoría con ese nombre");
        }

        Category category = new Category(user, request.name(), request.type(), request.color());
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    // LISTAR todas las del usuario
    public List<CategoryResponse> findAll(User user) {
        return categoryRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // OBTENER una concreta (solo si es del usuario)
    public CategoryResponse findById(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        return toResponse(category);
    }

    // ACTUALIZAR (solo si es del usuario)
    public CategoryResponse update(UUID id, CategoryRequest request, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        category.setName(request.name());
        category.setType(request.type());
        category.setColor(request.color());

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    // ELIMINAR (solo si es del usuario)
    public void delete(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        categoryRepository.delete(category);
    }

    // Conversión entidad → DTO
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor()
        );
    }
}