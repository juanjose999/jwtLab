package org.adaschool.api.data.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 */
@Service
public class UserServiceJPA implements UserService {

    final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public UserServiceJPA(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findById(String id) {
        return userRepository.findById(Long.parseLong(id));
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(UserEntity user) {
        userRepository.delete(user);
    }

    @Override
    public boolean validateUser(String email, String password) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        // Verificar si el usuario existe y si la contraseña coincide
        return optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPasswordHash());
    }


}
