package org.adaschool.api.security;

import org.adaschool.api.data.user.UserEntity;
import org.adaschool.api.data.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isPresent()) {
            UserEntity userEntity = optionalUser.get();
            List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());
            return new User(userEntity.getEmail(), userEntity.getPasswordHash(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
