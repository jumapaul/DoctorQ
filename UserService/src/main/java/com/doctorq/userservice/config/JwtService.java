package com.doctorq.userservice.config;

import com.doctorq.userservice.user.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${security.jwt.expiration-time}")
    private Long JWT_EXPIRATION_TIME;

    @Value("${security.jwt.refresh-token.expiration}")
    private Long REFRESH_TOKEN_EXPIRATION;

    public String extractRoles(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts.parser().keyLocator(keyId -> getSignInKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(
            UserDetails userDetails
    ) {
        return buildToken(userDetails, JWT_EXPIRATION_TIME);
    }

    public String generateRefreshToken(UserDetails userDetails) {

        return buildToken(userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(
            UserDetails userDetails,
            long expiration
    ) {
        Map<String, Object> claims = new HashMap<>();
        User user = (User) userDetails;
        claims.put("role", user.getRole().name());
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
