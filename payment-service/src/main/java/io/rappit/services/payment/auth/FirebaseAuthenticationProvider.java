package io.rappit.services.payment.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FirebaseAuthenticationProvider implements AuthenticationProvider {
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!authentication.isAuthenticated()) {
            try {
                FirebaseToken validateToken = firebaseAuth.verifyIdToken(authentication.getPrincipal().toString());
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                if(validateToken.isEmailVerified() && validateToken.getEmail().endsWith("@rappitpay.com")) {
                    authorities.add(new SimpleGrantedAuthority("ADMIN"));
                }
                authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
                return new FirebaseAuthenticationToken(
                        authorities,
                        validateToken,
                        authentication.getPrincipal().toString()
                );
            } catch (final FirebaseAuthException e) {
                throw new AuthenticationServiceException(e.getMessage());
            }
        }
        throw new AuthenticationCredentialsNotFoundException("Failed to authenticate " + authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FirebaseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
