package io.rappit.services.payment.auth;

import io.rappit.services.merchant.api.Merchants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class MerchantAuthenticationProvider implements AuthenticationProvider {
    private static final Logger log = LoggerFactory.getLogger(MerchantAuthenticationProvider.class);
    private final Merchants merchants;

    public MerchantAuthenticationProvider(Merchants merchants) {
        this.merchants = merchants;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Performing authentication for merchant: " + authentication.getPrincipal());
        log.info("Getting merchant by API key: " + merchants.findByApiKey(authentication.getPrincipal().toString()));
        if (!authentication.isAuthenticated()) {
            try {
                Authentication auth = new MerchantApiKeyAuthenticationToken(
                        Collections.singleton(new SimpleGrantedAuthority("MERCHANT")),
                        merchants.findByApiKey(authentication.getPrincipal().toString())
                );
                log.info("Authenticated: " + auth);
                return auth;
            } catch (final Exception e) {
                throw new AuthenticationServiceException(e.getMessage());
            }
        }
        throw new AuthenticationCredentialsNotFoundException("Failed to authenticate " + authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.info("Checking merchant authentication support: " + authentication + " is " + MerchantApiKeyAuthenticationToken.class.isAssignableFrom(authentication));
        return MerchantApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
