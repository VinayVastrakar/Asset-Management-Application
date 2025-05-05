package com.example.Assets.Management.App.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtUtil {
    private final String jwtSecret = "asdfgvhgvgkhafjhcaaaaaaaaaaaabbbbbbbbdjhhheeeeeeeeeeejjjjjjjjjjjkkkkkkkkkkkkkkkkkssssssssssskaaaaaaaaaaaaaaaaaaaaaaadddddddasdhbauhegfahdcbsjdvaghsdvaskjhsvgvrysecretkeysdscscscscscscscscscscscscscscscsdccccccccccccccccccccccccccccccccccccccccrfbfdgndfgsdgnnnnnnfsngsgnsgnsdljkfvbahvbjkdfbjasdhfajkldvbjhfbhsdbfvdfhlvafbvahldbfgvfhhadfbadrhbjkvndfkjnvjatgiah";
    private final long jwtExpirationMs = 86400000; // 1 day

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
