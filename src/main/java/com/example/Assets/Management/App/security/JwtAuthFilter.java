package com.example.Assets.Management.App.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null) {
                try {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        if (jwtUtil.validateToken(jwt, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = createAuthenticationToken(userDetails, request);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("Successfully authenticated user: {}", username);
                        } else {
                            logger.warn("Token validation failed for user: {}", username);
                        }
                    }
                } catch (ExpiredJwtException e) {
                    logger.error("JWT token has expired");
                    handleAuthenticationError(response, "Token has expired", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } catch (MalformedJwtException | SignatureException e) {
                    logger.error("Invalid JWT token");
                    handleAuthenticationError(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                } catch (UsernameNotFoundException e) {
                    logger.error("User not found: {}", e.getMessage());
                    handleAuthenticationError(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Authentication error: {}", e.getMessage());
            handleAuthenticationError(response, "Authentication failed", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    private void handleAuthenticationError(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        Map<String, String> errorResponse = Map.of("error", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
