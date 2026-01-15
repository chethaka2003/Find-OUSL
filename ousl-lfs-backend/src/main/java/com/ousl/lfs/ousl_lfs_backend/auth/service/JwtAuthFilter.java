package com.ousl.lfs.ousl_lfs_backend.auth.service;

import com.ousl.lfs.ousl_lfs_backend.auth.repo.RevokedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Do NOT require JWT for auth endpoints
        String path = request.getServletPath();
        return path != null && path.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // If no token, continue (Spring Security will reject protected endpoints)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extract token
        String token = authHeader.substring(7);

        // 3) Validate JWT signature + expiration
        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4) Extract JTI (token unique id) and block if revoked
        String jti = jwtService.extractJti(token);
        if (jti != null && revokedTokenRepository.existsById(jti)) {
            // Token is revoked -> treat as not authenticated
            filterChain.doFilter(request, response);
            return;
        }

        // 5) Extract email (subject)
        String email = jwtService.extractEmail(token);

        // 6) Load user details and set authentication into SecurityContext
        var userDetails = userDetailsService.loadUserByUsername(email);

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7) Continue request
        filterChain.doFilter(request, response);
    }
}
