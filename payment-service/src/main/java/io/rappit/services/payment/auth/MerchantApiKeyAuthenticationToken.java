package io.rappit.services.payment.auth;

import io.rappit.services.merchant.api.Merchant;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MerchantApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    public MerchantApiKeyAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        super.setAuthenticated(false);
    }

    public MerchantApiKeyAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Merchant merchant) {
        super(authorities);
        this.principal = merchant;
        super.setAuthenticated(true);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (authenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

}
