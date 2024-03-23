package com.flab.offcoupon.config.security.configs;

import com.flab.offcoupon.config.security.provider.CustomAuthenticationProvider;
import com.flab.offcoupon.util.bcrypt.BCryptPasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler customAuthenticationFailureHandler;
    private final AccessDeniedHandler customAccessDeniedHandler;
    private static final String LOGIN_URL = "/api/v1/members/login";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headerConfig ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                // resource에 대해서는 모든 요청 허용
                                .requestMatchers(
                                        PathRequest.toStaticResources().atCommonLocations()
                                ).permitAll()
                                .requestMatchers(
                                        "/",
                                        "/api/v1/members/signup",
                                        "/api/v1/event/**", // TODO : 테스트용
                                        "/api/v1/sse/**",
                                        "/main")
                                .permitAll()
                                .requestMatchers("/member").hasAnyRole("USER")
                                .requestMatchers("/admin").hasAnyRole("ADMIN")
                                // 그 외 모든 요청은 인증 완료
                                .anyRequest().authenticated()
                );

        http
                .formLogin(form -> form
                        .loginProcessingUrl(LOGIN_URL)
                        .defaultSuccessUrl("/api/v1/")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                );
        http
                .logout(logout -> logout
                        .logoutUrl("/api/v1/members/logout")
                        .logoutSuccessUrl(LOGIN_URL)
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSIONID")
                );
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(customAccessDeniedHandler)
                );
        http.
                rememberMe(rememberMe -> rememberMe
                        .rememberMeCookieName("remember")
                        .tokenValiditySeconds(3600)
                        .userDetailsService(userDetailsService)
                );
        http
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl(LOGIN_URL)
                );
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncryptor();
    }
}
