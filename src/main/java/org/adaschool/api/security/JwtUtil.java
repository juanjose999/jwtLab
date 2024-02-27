package org.adaschool.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.adaschool.api.controller.auth.TokenDto;
import org.adaschool.api.data.user.UserRoleEnum;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.adaschool.api.utils.Constants.CLAIMS_ROLES_KEY;


@Component
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private String secret;
    private long expiration;

    // Otros métodos y atributos...

    public Date getExpirationDate() {
        long expirationTimeInMilliseconds = Calendar.getInstance().getTimeInMillis() + expiration;
        return new Date(expirationTimeInMilliseconds);
    }

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public TokenDto generateToken(String username, List<? extends UserRoleEnum> roles) {
        Date expirationDate = jwtConfig.getExpirationDate();

        // Convertir la lista de roles a una lista de strings
        List<String> rolesAsString = roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        String token = Jwts.builder().subject(username)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .claim(CLAIMS_ROLES_KEY, rolesAsString)
                .signWith(jwtConfig.getSigningKey())
                .compact();

        return new TokenDto(token, expirationDate);
    }

    public Claims extractAndVerifyClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}

