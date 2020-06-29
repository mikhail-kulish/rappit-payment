package io.rappit.services.payment.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@EnableWebSecurity
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private FirebaseAuthenticationProvider firebaseAuthenticationProvider;
    @Autowired
    private MerchantAuthenticationProvider merchantAuthenticationProvider;
    @Autowired
    private RestTemplate restTemplate;

    public AuthenticationConfig() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setProjectId("rappit")
                .setDatabaseUrl("https://rappit.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and()
                .authenticationProvider(firebaseAuthenticationProvider)
                .authenticationProvider(merchantAuthenticationProvider)
                .addFilterBefore(new FirebaseAuthFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new MerchantAuthFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new MerchantCustomerAuthFilter(restTemplate), FilterSecurityInterceptor.class)
                .authorizeRequests()
                .mvcMatchers("/**").permitAll();
    }

}
