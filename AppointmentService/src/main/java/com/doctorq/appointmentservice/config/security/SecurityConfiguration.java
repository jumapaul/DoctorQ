package com.doctorq.appointmentservice.config.security;

import com.doctorq.appointmentservice.appointment.dtos.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/actuator/health", "/actuator/info").permitAll()
                                .requestMatchers("/api/v1/**").authenticated()
                                .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(((request, response, authException) -> {
                    if (authException.getMessage() != null) {
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.setContentType("application/json");

                        ApiResponse<Object> apiResponse = new ApiResponse<>(
                                HttpStatus.FORBIDDEN.value(),
                                authException.getMessage(),
                                null
                        );

                        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                    }
                })));

        return http.build();
    }
}
