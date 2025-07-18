package com.example.demo.config;

import com.example.demo.common.jwt.TokenComponent;
import com.example.demo.common.oauth2.CustomSuccessHandler;
import com.example.demo.common.security.filter.JWTFilter;
import com.example.demo.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomSuccessHandler customSuccessHandler;
  private final TokenComponent jwtUtil;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {

          CorsConfiguration configuration = new CorsConfiguration();

          configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
          configuration.setAllowedMethods(Collections.singletonList("*"));
          configuration.setAllowCredentials(true);
          configuration.setAllowedHeaders(Collections.singletonList("*"));
          configuration.setMaxAge(3600L);

          configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
          configuration.setExposedHeaders(Collections.singletonList("Authorization"));

          return configuration;
        }));

    return http.build();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
        .oauth2Login(oauth2 ->
            oauth2.userInfoEndpoint(userInfoEndpointConfig ->
                    userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/").permitAll()
            .anyRequest().authenticated())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();
  }

}
