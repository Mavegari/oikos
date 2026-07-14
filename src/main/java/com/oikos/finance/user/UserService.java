package com.oikos.finance.user;

import com.oikos.finance.security.JwtService;
import com.oikos.finance.user.dto.LoginRequest;
import com.oikos.finance.user.dto.AuthResponse;
import com.oikos.finance.user.dto.RegisterRequest;
import com.oikos.finance.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar el usuario por email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        // 2. Comprobar que la contraseña coincide con el hash guardado
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // 3. Generar el token
        String token = jwtService.generateToken(user.getEmail());

        // 4. Devolverlo
        return new AuthResponse(token);
    }
}