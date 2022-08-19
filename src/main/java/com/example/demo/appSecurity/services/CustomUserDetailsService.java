/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.services;

import com.example.demo.appSecurity.repositories.UserRepository;
import com.example.demo.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new ApiException("Invalid email", HttpStatus.UNAUTHORIZED));
    }
}
