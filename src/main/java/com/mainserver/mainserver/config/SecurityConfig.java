package com.mainserver.mainserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityConfig(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public SecurityFilterChain configure(AuthenticationManagerBuilder authentication, HttpSecurity http) throws Exception {
        authentication.authenticationProvider(authenticationProvider);
        return http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/admin/edit/**").hasRole("ADMIN");
                    auth.requestMatchers("/admin/devices/**","/admin/objects/**").hasAnyRole("ADMIN","MODERATOR");
                    auth.requestMatchers("/admin/**").hasAnyRole("ADMIN","MODERATOR","HELPER");
                    auth.anyRequest().permitAll();
                })
                .formLogin()
                .defaultSuccessUrl("/admin",true)
                .failureUrl("/login?error").and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling().accessDeniedPage("/admin/accessDenied")
                .and()
                .headers(headers->headers.frameOptions().sameOrigin())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
