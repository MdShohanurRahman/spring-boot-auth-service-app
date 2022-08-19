/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.services;

import com.example.demo.appSecurity.entities.Role;
import com.example.demo.appSecurity.entities.User;
import com.example.demo.appSecurity.enums.RoleName;
import com.example.demo.appSecurity.jwt.JwtProvider;
import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;
import com.example.demo.appSecurity.repositories.RoleRepository;
import com.example.demo.appSecurity.repositories.UserRepository;
import com.example.demo.exceptions.BadRequestApiException;
import com.example.demo.exceptions.NotFoundApiException;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final CustomAuthenticationProvider authenticationProvider;
    private final JwtProvider jwtProvider;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
            CustomAuthenticationProvider authenticationProvider,
            JwtProvider jwtProvider,
            UserRepository userRepository,
            RoleRepository roleRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder) {
        this.authenticationProvider = authenticationProvider;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public UserModel register(UserModel model) {
        if (userRepository.existsByEmail(model.getEmail())) {
            throw new BadRequestApiException("Email Already exists");
        }

        User user = new User();

        Set<Role> roles = new HashSet<>();
        Role viewer = roleRepository.findByName(RoleName.ROLE_VIEWER)
                .orElseThrow(() -> new NotFoundApiException("Role not found"));
        roles.add(viewer);

        user.setUuid(UUID.randomUUID().toString());
        user.setEmail(model.getEmail());
        user.setPassword(passwordEncoder.encode(model.getPassword()));
        user.setRoles(roles);

        user = userRepository.save(user);
        model = modelMapper.map(user, UserModel.class);

        return model;
    }

    @Override
    public TokenModel getToken(LoginModel model) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(model.getEmail(), model.getPassword());
        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new TokenModel(token);
    }
}
