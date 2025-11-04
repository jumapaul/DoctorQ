package com.doctorq.userservice.config;

import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        try {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

            String registrationId = token.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(registrationId, token.getName());

            String email;
            String firstname;
            String lastname;

            if ("google".equals(registrationId)) {
                email = (String) principal.getAttributes().get("email");
                firstname = (String) principal.getAttributes().get("given_name");
                lastname = (String) principal.getAttributes().get("family_name");
            }

            else if ("github".equals(registrationId)) {
                email = (String) principal.getAttributes().get("email");
                firstname = (String) principal.getAttributes().get("name");
                lastname = "";

                if (email == null || email.isEmpty()) {
                    String accessToken = client.getAccessToken().getTokenValue();
                    URL url = new URL("https://api.github.com/user/emails");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Authorization", "Bearer " + accessToken);

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String responseBody = reader.lines().collect(Collectors.joining());
                        List<Map<String, Object>> emails = objectMapper.readValue(responseBody, List.class);

                        Optional<Map<String, Object>> primaryEmail = emails.stream()
                                .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                                .findFirst();

                        if (primaryEmail.isPresent()) {
                            email = (String) primaryEmail.get().get("email");
                        } else if (!emails.isEmpty()) {
                            email = (String) emails.get(0).get("email");
                        }
                    }
                }
            } else {
                throw new RuntimeException("Failed to retrieve user email from " + registrationId);
            }

            String finalEmail = email;
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .firstname(firstname != null ? firstname : "")
                        .lastname(lastname != null ? lastname : "")
                        .email(finalEmail)
                        .isEnabled(true)
                        .role(Roles.USER)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .build();
                return userRepository.save(newUser);
            });

            String appJwt = jwtService.generateToken(user);

            Map<String, Object> payload = new HashMap<>();
            payload.put("token", appJwt);
            payload.put("profile", principal.getAttributes());
            payload.put("access_token", client.getAccessToken().getTokenValue());
            payload.put("provider", registrationId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), payload);
            response.getWriter().flush();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
