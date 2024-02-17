package org.adaschool.api.controller.auth;

import org.adaschool.api.data.user.UserEntity;
import org.adaschool.api.data.user.UserService;
import org.adaschool.api.exception.InvalidCredentialsException;
import org.adaschool.api.security.JwtUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;


    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @PostMapping
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        Optional<UserEntity> optionalUser = userService.findByEmail(loginDto.getUsername());
        if(optionalUser.isPresent()){
            UserEntity userEntity = optionalUser.get();
            if(BCrypt.checkpw(loginDto.getPassword(), userEntity.getPasswordHash())){
                TokenDto tokenDto = jwtUtil.generateToken(userEntity.getEmail(), userEntity.getRoles());
                return ResponseEntity.ok(tokenDto);
            }else {
                throw new InvalidCredentialsException();
            }
        }else {
            throw new InvalidCredentialsException();
        }
    }


}
