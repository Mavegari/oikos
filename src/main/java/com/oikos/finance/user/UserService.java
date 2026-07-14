package com.oikos.finance.user;

import com.oikos.finance.user.dto.RegisterRequest;
import com.oikos.finance.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        // 1. Comprobar que el email no esté ya en uso
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        // 2. Hashear la contraseña (nunca se guarda en claro)
        String hashedPassword = passwordEncoder.encode(request.password());

        // 3. Crear y guardar el usuario
        User user = new User(request.email(), hashedPassword);
        User savedUser = userRepository.save(user);

        // 4. Devolver una vista pública, sin el hash
        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }
}