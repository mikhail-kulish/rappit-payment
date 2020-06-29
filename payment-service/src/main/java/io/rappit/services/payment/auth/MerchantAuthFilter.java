package io.rappit.services.payment.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Predicate;

public class MerchantAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(MerchantAuthFilter.class);
    private static String HEADER_NAME = "X-rappit-Merchant-ApiKey";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> xAuth = Optional.ofNullable(request.getHeader(HEADER_NAME));
        log.info("Filtering authentication request " + request.getServletPath() + " api key: " + xAuth);
        if (xAuth.filter(Predicate.not(String::isBlank)).isPresent()) {
            Authentication auth = new MerchantApiKeyAuthenticationToken(xAuth.get());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !(
                request.getServletPath().toLowerCase().equals("/v1/spf/order") ||
                        request.getServletPath().toLowerCase().startsWith("/v1/merchant")
        );
    }
}
