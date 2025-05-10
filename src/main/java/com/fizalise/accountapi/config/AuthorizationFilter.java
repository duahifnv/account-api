package com.fizalise.accountapi.config;

import com.fizalise.accountapi.entity.User;
import com.fizalise.accountapi.service.JwtService;
import com.fizalise.accountapi.service.user.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;
    private final HandlerExceptionResolver resolver;
    private final UserService userService;

    @Autowired
    public AuthorizationFilter(JwtService jwtService,
                               @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                               UserService userService) {
        this.jwtService = jwtService;
        this.resolver = resolver;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = authHeader.substring(BEARER_PREFIX.length());

            var tokenSubject = jwtService.extractSubject(jwt);
            long userId = Long.parseLong(tokenSubject);

            User user = userService.findByIdWithCollections(userId)
                    .orElseThrow(() -> new JwtException(
                            "Пользователь с id: %d отсутствует в системе".formatted(userId))
                    );
            String username = userService.getUserFirstEmail(user);

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Set.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );
            filterChain.doFilter(request, response);
        } catch (NumberFormatException | JwtException | ServletException | IOException e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}
