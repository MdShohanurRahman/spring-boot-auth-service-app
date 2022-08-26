/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.services;

import com.example.demo.appSecurity.entities.PasswordResetToken;
import com.example.demo.appSecurity.entities.Role;
import com.example.demo.appSecurity.entities.User;
import com.example.demo.appSecurity.entities.VerificationToken;
import com.example.demo.appSecurity.enums.RoleName;
import com.example.demo.appSecurity.jwt.JwtProvider;
import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.PasswordModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;
import com.example.demo.appSecurity.repositories.PasswordResetTokenRepository;
import com.example.demo.appSecurity.repositories.RoleRepository;
import com.example.demo.appSecurity.repositories.UserRepository;
import com.example.demo.appSecurity.repositories.VerificationTokenRepository;
import com.example.demo.exceptions.ApiException;
import com.example.demo.exceptions.BadRequestApiException;
import com.example.demo.exceptions.NotFoundApiException;
import com.example.demo.services.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final AppAuthenticationProvider authenticationProvider;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;

    public UserServiceImpl(
            AppAuthenticationProvider authenticationProvider,
            JwtProvider jwtProvider,
            UserRepository userRepository,
            RoleRepository roleRepository,
            VerificationTokenRepository verificationTokenRepository,
            PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder,
            EmailSenderService emailSenderService
    ) {
        this.authenticationProvider = authenticationProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.emailSenderService = emailSenderService;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User register(UserModel model, String baseUrl) {
        if (userRepository.existsByEmail(model.getEmail())) {
            throw new ApiException("Email Already exists", HttpStatus.CONFLICT);
        }

        User user = new User();
        Role viewer = roleRepository.findByName(RoleName.ROLE_VIEWER)
                .orElseThrow(() -> new NotFoundApiException("Role not found"));

        user.setUuid(UUID.randomUUID().toString());
        user.setEmail(model.getEmail());
        user.setPassword(passwordEncoder.encode(model.getPassword()));
        user.setRoles(Set.of(viewer));

        userRepository.save(user);

        // save verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);

        // send token
        String link = baseUrl + "/auth/verifyRegistration?token=" + token;
        emailSenderService.send(
                user.getEmail(),
                "Confirm your email",
                buildEmail(user.getEmail(), link, verificationToken.getExpirationTime())
        );

        return user;
    }

    @Override
    public TokenModel getJwtToken(LoginModel model) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(model.getEmail(), model.getPassword());
        Authentication authentication = authenticationProvider.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new TokenModel(token);
    }

    @Override
    public Boolean isValidVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) return false;

        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            log.error("verification token expired");
            return false;
        }

        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }

    @Override
    public VerificationToken resendVerificationToken(String oldToken, String baseUrl) {
        VerificationToken token = verificationTokenRepository.findByToken(oldToken);

        if (token == null) {
            throw new BadRequestApiException("Invalid token");
        }

        token.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(token);

        User user = token.getUser();

        String link = baseUrl + "/auth/verifyRegistration?token=" + token;
        emailSenderService.send(
                user.getEmail(),
                "Confirm your email",
                buildEmail(user.getEmail(), link, token.getExpirationTime())
        );

        return token;
    }

    @Override
    public PasswordResetToken sendPasswordResetToken(PasswordModel passwordModel, String baseUrl) {
        User user = userRepository.findByEmail(passwordModel.getEmail()).orElseThrow(() -> new BadRequestApiException("Invalid email"));
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetToken = passwordResetTokenRepository.save(passwordResetToken);

        String link = baseUrl + "/auth/savePassword?token=" + token;
        emailSenderService.send(
                user.getEmail(),
                "Reset Password",
                buildEmail(user.getEmail(), link, passwordResetToken.getExpirationTime())
        );
        return passwordResetToken;
    }

    @Override
    public Boolean isValidatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) return false;

        Calendar calendar = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            log.error("token expired");
            return false;
        }

        return true;
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private String buildEmail(String name, String link, Date expirationTime) {
        return
                "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p>" +
                "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Please click on the below link: </p>" +
                "<blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\">" +
                "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> " +
                "<a href=\"" + link + "\">Click here</a> </p>" +
                "</blockquote>\n Link will expire in " + expirationTime +
                "<p>Thanks</p>";
    }

}
