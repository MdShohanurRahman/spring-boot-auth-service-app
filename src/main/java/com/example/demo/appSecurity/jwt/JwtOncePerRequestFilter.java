/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.jwt;

import com.example.demo.appSecurity.services.AppUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtOncePerRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    public JwtOncePerRequestFilter(
            AppUserDetailsService userDetailsService,
            JwtProvider jwtProvider
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        // Get jwt token and validate
        final String token = header.substring(7);
        String userName = null;

        try {
            userName = jwtProvider.getUserNameFromToken(token);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException | SignatureException e) {
            request.setAttribute("exp", "Invalid token");
        } catch (ExpiredJwtException e) {
            request.setAttribute("exp", "Token expired");
        }

        if (userName == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get user identity and set it on the spring security context
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (jwtProvider.validateToken(token, userDetails)) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } else {
            request.setAttribute("exp", "Invalid token");
        }

        filterChain.doFilter(request, response);
    }
}
