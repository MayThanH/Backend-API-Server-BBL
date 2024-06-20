package com.may.app.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/users")
                .and()
                .logout()
                .logoutSuccessHandler(oidcLogoutSuccessHandler());
    }

    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository());
        successHandler.setPostLogoutRedirectUri("http://localhost:3000");
        return successHandler;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(this.auth0ClientRegistration());
    }

    private ClientRegistration auth0ClientRegistration() {
        return ClientRegistration.withRegistrationId("auth0")
                .clientId("H9F6QG5SzTKMv0tbmgxLj9LjG1EKVllA")
                .clientName("Auth0")
                .scope("openid", "profile", "email")
                .authorizationUri("https://dev-yg.us.auth0.com/authorize")
                .tokenUri("https://dev-yg.us.auth0.com/oauth/token")
                .userInfoUri("https://dev-yg.us.auth0.com/userinfo")
                .jwkSetUri("https://dev-yg.us.auth0.com/.well-known/jwks.json")
                .redirectUriTemplate(
                        ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/login/oauth2/code/auth0")
                                .buildAndExpand("auth0")
                                .toUriString())
//                .redirectUri("http://localhost:8080/login/oauth2/code/auth0")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }
}