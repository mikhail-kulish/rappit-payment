package io.rappit.services.payment.auth;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String credentials;

    public FirebaseAuthenticationToken(String token) {
        super(null);
        this.principal = token;
        this.credentials = null;
        setAuthenticated(false);
    }

    public FirebaseAuthenticationToken(Collection<? extends GrantedAuthority> authorities, FirebaseToken token, String credentials) {
        super(authorities);
        this.principal = token.getUid();
        setDetails(token);
        this.credentials = credentials;
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
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
