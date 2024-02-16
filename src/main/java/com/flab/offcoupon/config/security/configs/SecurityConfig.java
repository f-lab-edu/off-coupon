package com.flab.offcoupon.config.security.configs;

import com.flab.offcoupon.config.security.provider.CustomAuthenticationProvider;
import com.flab.offcoupon.util.bcrypt.BCryptPasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
    private final AccessDeniedHandler accessDeniedHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .headers(headerConfig ->
                        headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/members/signup").permitAll()
                                .requestMatchers("/member").hasAnyRole("USER")
                                .anyRequest().authenticated()
                );

        http    // 로그인 설정
                .formLogin(form -> form //  스프링 시큐리티가 제공하는 로그인 페이지 주소는 "/login"이다.
                        .defaultSuccessUrl("/")// 로그인 성공 후 이동할 URL
                        .usernameParameter("email") // 로그인 페이지의 username input name
                        .passwordParameter("password") // 로그인 페이지의 password input name
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                );
        http
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL
                        .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 URL
                        .invalidateHttpSession(true) // 로그아웃 후 세션 무효화
                        .deleteCookies("SESSIONID") // 로그아웃 후 쿠키 삭제
                );
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                ); // 접근 권한이 없는 경우 처리

        http.
                rememberMe(rememberMe -> rememberMe
                        .rememberMeCookieName("remember") // remember-me 쿠키의 키
                        .tokenValiditySeconds(3600) // remember-me 쿠키의 유효시간
                        .userDetailsService(userDetailsService)// remember-me 쿠키 생성 시 사용할 UserDetailsService
                );
        http
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1) // 최대 세션 허용 개수
                        .maxSessionsPreventsLogin(false) // 최대 세션 허용 개수 초과 시 로그인 차단 여부
                        .expiredUrl("/login") // 세션이 만료된 경우 이동할 URL
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