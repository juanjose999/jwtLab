package org.adaschool.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.adaschool.api.controller.auth.TokenAuthentication;
import org.adaschool.api.exception.TokenExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static org.adaschool.api.utils.Constants.CLAIMS_ROLES_KEY;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        try{
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                String jwt = authorizationHeader.substring(7);
                Claims claims = jwtUtil.extractAndVerifyClaims(jwt);
                String username = claims.getSubject();
                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                    List<String> roles = claims.get(CLAIMS_ROLES_KEY, List.class);
                    TokenAuthentication tokenAuthentication = new TokenAuthentication(jwt, username, roles);
                    SecurityContextHolder.getContext().setAuthentication(tokenAuthentication);
                }
            }
        }catch (ExpiredJwtException e){
            throw new TokenExpiredException();
        }
        filterChain.doFilter(request, response);
    }
}
