/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity;

import com.example.demo.appSecurity.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.appSecurity.jwt.JwtOncePerRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MainAppSecurity {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtOncePerRequestFilter jwtOncePerRequestFilter;

    private static final String[] PUBLIC_URLS = {
            "/",
            "/api/v1/auth/token",
            "/api/v1/auth/register"
    };

    public MainAppSecurity(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtOncePerRequestFilter jwtOncePerRequestFilter
    ) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtOncePerRequestFilter = jwtOncePerRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return jwtSecurityFilterChain(httpSecurity);
        // return basicSecurityFilterChain(httpSecurity);
    }

    private SecurityFilterChain jwtSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        /*
         * Enable CORS and disable CSRF
         * Set session management to stateless
         * Set unauthorized requests exception handler
         * Set permissions on endpoints
         * Add JWT filter
         * */
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(PUBLIC_URLS).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    private SecurityFilterChain basicSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .antMatchers(PUBLIC_URLS).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                )
                .and()
                .httpBasic();

        return httpSecurity.build();
    }
}
