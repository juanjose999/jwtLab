package org.adaschool.api.controller.user;

import jakarta.annotation.security.RolesAllowed;
import org.adaschool.api.data.user.UserEntity;
import org.adaschool.api.data.user.UserRoleEnum;
import org.adaschool.api.data.user.UserService;
import org.adaschool.api.exception.UserWithEmailAlreadyRegisteredException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.adaschool.api.utils.Constants.ADMIN_ROLE;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        loadSampleUsers();
    }

    public void loadSampleUsers() {
        if (passwordEncoder != null) {
            UserEntity userEntity = new UserEntity("Ada Lovelace", "ada@mail.com", passwordEncoder.encode("passw0rd"));
            userService.save(userEntity);
            UserEntity adminUserEntity = new UserEntity("Ada Admin", "admin@mail.com", passwordEncoder.encode("passw0rd"));
            adminUserEntity.addRole(UserRoleEnum.ADMIN);
            userService.save(adminUserEntity);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {
        Optional<UserEntity> optionalUser = userService.findById(id);
        return optionalUser.map(ResponseEntity::ok).orElseGet(() -> new ResponseEntity<>(null, HttpStatusCode.valueOf(404)));
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserDto userDto) {
        userService.findByEmail(userDto.getEmail()).ifPresent(user -> {
            throw new UserWithEmailAlreadyRegisteredException();
        });
        String passwordHash = passwordEncoder.encode(userDto.getPassword());
        UserEntity userEntity = new UserEntity(userDto.getName(), userDto.getEmail(), passwordHash);
        UserEntity user = userService.save(userEntity);
        return ResponseEntity.ok(user);
    }

    @RolesAllowed(ADMIN_ROLE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String id) {
        Optional<UserEntity> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            userService.delete(optionalUser.get());
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);

    }

}
