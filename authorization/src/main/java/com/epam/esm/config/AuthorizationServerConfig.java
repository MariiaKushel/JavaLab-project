package com.epam.esm.config;

import com.epam.esm.details.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.security.KeyPair;
import java.util.List;

@Import(AuthorizationServerEndpointsConfiguration.class)
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String PERMIT_ALL = "permitAll()";
    private static final String IS_AUTHENTICATED = "isAuthenticated()";
    private static final String CLIENT_ID = "clientApi";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String DEFAULT_SCOPE = "all";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    private static final int ACCESS_TOKEN_VALIDITY = 2000;
    private static final int REFRESH_TOKEN_VALIDITY = 2000;

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private KeyPair keyPair;
    private CustomUserDetailsService service;
    private CustomTokenEnhancer tokenEnhancer;

    @Autowired
    public AuthorizationServerConfig(AuthenticationManager authenticationManager,
                                     PasswordEncoder passwordEncoder,
                                     KeyPair keyPair,
                                     CustomUserDetailsService service,
                                     CustomTokenEnhancer tokenEnhancer){

        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.keyPair = keyPair;
        this.service = service;
        this.tokenEnhancer = tokenEnhancer;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security
                .tokenKeyAccess(PERMIT_ALL)
                .checkTokenAccess(IS_AUTHENTICATED)
                .allowFormAuthenticationForClients();
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient(CLIENT_ID)
                .secret(this.passwordEncoder.encode(CLIENT_SECRET))
                .scopes(DEFAULT_SCOPE)
                .authorizedGrantTypes(GRANT_TYPE_PASSWORD, GRANT_TYPE_REFRESH_TOKEN)
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY)
                .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(List.of(tokenEnhancer, accessTokenConverter()));
        endpoints
                .authenticationManager(this.authenticationManager)
                .tokenEnhancer(tokenEnhancerChain)
                .accessTokenConverter(accessTokenConverter())
                .userDetailsService(this.service)
                .tokenStore(tokenStore());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(this.keyPair);
        return converter;
    }

}