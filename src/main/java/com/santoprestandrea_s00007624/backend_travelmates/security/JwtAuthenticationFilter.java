package com.santoprestandrea_s00007624.backend_travelmates.security;

import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import com.santoprestandrea_s00007624.backend_travelmates.service.JwtService;
import com.santoprestandrea_s00007624.backend_travelmates.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * THIS METHOD IS EXECUTED FOR EVERY HTTP REQUEST
     *
     * FLOW:
     * 1. Extracts the token from the "Authorization" header
     * 2. If there's no token → let it pass (public endpoints)
     * 3. If there's a token → validate it
     * 4. If valid → load the user and authenticate them
     * 5. If invalid → block (401 Unauthorized)
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // STEP 1: Extract the token from the header
            String token = extractTokenFromRequest(request);

            // STEP 2: If there's no token, proceed without authentication
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // STEP 3: Validate the token
            if (!jwtService.validateToken(token)) {
                logger.warn("Invalid or expired JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }

            // STEP 4: Extract the email from the token
            String email = jwtService.getEmailFromToken(token);

            // STEP 5: Load the user from the database
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // STEP 6: Create the "Authentication" object for Spring Security
            // This tells Spring: "This user is authenticated and has this role"
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, // Authenticated user
                    null, // Credentials (not needed, already verified)
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())));

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // STEP 7: Save the authentication in the security context
            // From this point on, Spring knows who the current user is
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.debug("User authenticated: {} with role: {}", email, user.getRole());

            // STEP 8: Proceed with the request
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            logger.error("Error during JWT authentication: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication error\"}");
        }
    }

    /**
     * EXTRACTS THE TOKEN FROM THE "Authorization" HEADER
     *
     * The header must be in the format: "Bearer eyJhbGciOiJIUzI1NiIs..."
     * This method removes "Bearer " and returns only the token.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Removes "Bearer "
        }

        return null;
    }

    /**
     * PUBLIC ENDPOINTS THAT DON'T REQUIRE AUTHENTICATION
     *
     * If the request goes to one of these endpoints, the filter is NOT executed.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return pathMatcher.match("/api/auth/**", path) || // Login and registration
                pathMatcher.match("/api/public/**", path) || // Public endpoints
                pathMatcher.match("/error", path) || // Error page
                pathMatcher.match("/h2-console/**", path); // H2 Console (dev only)
    }
}
