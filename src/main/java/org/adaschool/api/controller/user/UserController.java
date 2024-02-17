package org.adaschool.api.controller.user;

import jakarta.annotation.security.RolesAllowed;
import org.adaschool.api.data.user.UserEntity;
import org.adaschool.api.data.user.UserRoleEnum;
import org.adaschool.api.data.user.UserService;
import org.adaschool.api.exception.UserWithEmailAlreadyRegisteredException;
import org.springframework.http.HttpStatus;
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
        UserEntity userEntity = new UserEntity("Juan", "juan@gmail.com",passwordEncoder.encode("clave123"));
        userService.save(userEntity);
        UserEntity userAdmin = new UserEntity("Harrison", "harri@gmail.com" ,passwordEncoder.encode("clave123" ));
        userAdmin.addRole(UserRoleEnum.ADMIN);
        userService.save(userAdmin);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable String id) {
        try{
            Optional<UserEntity> findUserById = userService.findById(id);
            if(findUserById.isPresent()){
                return new ResponseEntity<>(findUserById.get(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (NumberFormatException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserDto userDto) {
        if(userService.findByEmail(userDto.getEmail()).isPresent()){
            throw new UserWithEmailAlreadyRegisteredException(userDto.getEmail());
        }
        UserEntity userEntity = new UserEntity(userDto.getName(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()));

        UserEntity saveUser = userService.save(userEntity);
        return ResponseEntity.ok(saveUser);
    }

    @RolesAllowed(ADMIN_ROLE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable String id) {
        try{
            Long userId = Long.parseLong(id);
            // Buscar el usuario por ID utilizando el servicio
            UserEntity userToDelete = userService.findById(String.valueOf(userId)).orElse(null);

            if (userToDelete != null) {
                // Si se encuentra el usuario, eliminarlo utilizando el servicio
                userService.delete(userToDelete);
                return ResponseEntity.ok(true);
            } else {
                // Si no se encuentra el usuario, devuelve una respuesta de no encontrado
                return ResponseEntity.ok(false);
            }

        } catch (NumberFormatException e) {
            // Manejar el caso en que el ID no sea un número válido
            return ResponseEntity.ok(false);
        }
    }

}
