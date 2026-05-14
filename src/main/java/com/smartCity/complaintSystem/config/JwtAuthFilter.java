package com.smartCity.complaintSystem.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartCity.complaintSystem.service.JwtService;
import com.smartCity.complaintSystem.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService; 
    
    @Autowired
    private TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String email;

        // ✅ Log request
        System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());
        System.out.println("Auth Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token found");
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        System.out.println("Token: " + token.substring(0, Math.min(20, token.length())) + "...");

        if (blacklistService.isBlacklisted(token)) {
            System.out.println("Token is blacklisted");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        }

        try {
            email = jwtService.extractEmail(token);
            System.out.println("Extracted email: " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                System.out.println("Loaded user: " + userDetails.getUsername());
                System.out.println("Authorities: " + userDetails.getAuthorities());

                if (email.equals(userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(
                    	    "Current Auth: " +
                    	    SecurityContextHolder.getContext()
                    	        .getAuthentication()
                    	        .getAuthorities()
                    	);
                    System.out.println("Authentication set successfully");
                }
            }

        } catch (Exception e) {
            System.out.println("JWT Error: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

 
    
}