package com.haneen.dsa.security;

import com.haneen.dsa.principal.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Jws<Claims> jws = jwtUtil.validateAndParse(token); // validates signature + exp
                Claims claims = jws.getBody();

                // parse userId (JJWT may return Integer or Long depending on serializer)
                Object userIdObj = claims.get("userId");
                Long userId = null;
                if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj != null) {
                    userId = Long.valueOf(userIdObj.toString());
                }

                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                // Build principal and authorities
                UserPrincipal principal = new UserPrincipal(userId, username, role);
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // Create authentication and set into SecurityContext
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (JwtException | IllegalArgumentException ex) {
                // Invalid token: clear context and return 401
                SecurityContextHolder.clearContext();
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Invalid or expired token");
                return;
            }
        }

        // Continue filter chain (if no token, SecurityConfig will decide access)
        chain.doFilter(req, res);
    }
}
