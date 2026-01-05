package com.thesharehub.TheShareHub.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip JWT check for public endpoints
        if (path.equals("/users") && request.getMethod().equalsIgnoreCase("POST") ||
                path.equals("/users/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        if (request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }
        if(token != null && jwtUtil.isTokenValid(token)) {
            Long userId = jwtUtil.extractUserId(token);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                SecurityContextHolder.clearContext();

//                ResponseCookie expiredCookie = ResponseCookie.from("token", "")
//                        .httpOnly(true)
//                        .secure(false)
//                        .path("/")
//                        .maxAge(0)
//                        .sameSite("Lax")
//                        .build();
//                response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
            }

        filterChain.doFilter(request, response);
    }
}
