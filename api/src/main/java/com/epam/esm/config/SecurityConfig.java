package com.epam.esm.config;

import com.epam.esm.enumeration.AppRole;
import com.epam.esm.properties.JwtProperty;
import com.epam.esm.security.CustomAccessDeniedHandler;
import com.epam.esm.security.JwtAuthenticationEntryPoint;
import com.epam.esm.security.JwtAuthenticationFilter;
import com.epam.esm.security.JwtValidationFilter;
import com.epam.esm.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Class represent security configuration.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private SecurityService service;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private JwtValidationFilter jwtValidationFilter;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.service).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository cookieCsrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        cookieCsrfTokenRepository.setCookiePath("/");
        http
                .csrf().csrfTokenRepository(cookieCsrfTokenRepository)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.POST, "/registration")
                .not().fullyAuthenticated()

                .antMatchers(HttpMethod.GET, "/certificates", "/certificates/**", "/tags", "/tags/**")
                .permitAll()

                .antMatchers(HttpMethod.POST, "/certificates/**", "/tags/**")
                .hasAuthority(AppRole.ROLE_ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/certificates/**", "/tags/**")
                .hasAuthority(AppRole.ROLE_ADMIN.name())
                .antMatchers(HttpMethod.PATCH, "/certificates/**")
                .hasAuthority(AppRole.ROLE_ADMIN.name())
                .antMatchers(HttpMethod.GET, "/admin", "/admin/users/**/orders", "/admin/users/**/orders/**")
                .hasAuthority(AppRole.ROLE_ADMIN.name())

                .antMatchers(HttpMethod.GET, "/user", "/user/orders", "/user/orders/**")
                .hasAuthority(AppRole.ROLE_USER.name())
                .antMatchers(HttpMethod.POST, "/user/orders")
                .hasAuthority(AppRole.ROLE_USER.name())

                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(this.jwtAuthenticationEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(this.customAccessDeniedHandler)
                .and()
                .logout().deleteCookies(this.jwtProperty.getCookieName())
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .and()
                .addFilter(this.jwtAuthenticationFilter)
                .addFilterBefore(this.jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
