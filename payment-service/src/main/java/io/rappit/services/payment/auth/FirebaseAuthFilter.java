package io.rappit.services.payment.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class FirebaseAuthFilter extends OncePerRequestFilter {
    private static String HEADER_NAME = "X-Authorization-Rappit";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> xAuth = Optional.ofNullable(request.getHeader(HEADER_NAME));
        if (xAuth.isPresent() && !xAuth.get().trim().isEmpty()) {
            Authentication auth = new FirebaseAuthenticationToken(xAuth.get());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

}
