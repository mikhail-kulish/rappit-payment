package io.rappit.services.payment.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class MerchantCustomerAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(MerchantCustomerAuthFilter.class);
    private static final String root = "/v1/portal/merchant";
    private final RestTemplate restTemplate;

    public MerchantCustomerAuthFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getMethod().equals("OPTIONS") || !request.getServletPath().toLowerCase().startsWith(root);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String[] params = request.getServletPath().toLowerCase().split(root, 2);
        if (params.length != 2) {
            log.info("Skipping merchant portal request: params[0]={}, params[1]={}", params[0], params[1]);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String[] merchantIdParams = params[1].split("/", 3);
        if (merchantIdParams.length != 3 || !Pattern.matches("\\d+", merchantIdParams[1])) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Filtering merchant [{}] user request path: {} for principal: {}", merchantIdParams[1], request.getServletPath(), authentication);
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")) || hasCustomerAccess(authentication.getPrincipal().toString(), merchantIdParams[1])) {
            filterChain.doFilter(request, response);
        } else {
            log.info("Skipping merchant portal request: principal={} merchant={}", authentication.getPrincipal().toString(), merchantIdParams[1]);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean hasCustomerAccess(String customerId, String merchantId) {
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(
                "http://merchant-lb/merchant/v1/{merchantId}/owner",
                Map.class,
                merchantId
        );
        if(responseEntity.getStatusCodeValue() == 200 && responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
            log.info("Getting merchant {} owner: {}", merchantId, responseEntity.getBody().get("owner"));
            return customerId.equals(responseEntity.getBody().get("owner"));
        }
        return false;
    }
}
