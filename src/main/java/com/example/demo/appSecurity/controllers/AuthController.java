/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.controllers;

import com.example.demo.appSecurity.entities.PasswordResetToken;
import com.example.demo.appSecurity.entities.User;
import com.example.demo.appSecurity.entities.VerificationToken;
import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.PasswordModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;
import com.example.demo.appSecurity.services.UserService;
import com.example.demo.exceptions.BadRequestApiException;
import com.example.demo.exceptions.NotFoundApiException;
import com.example.demo.utils.AppUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenModel> token(@Valid @RequestBody LoginModel model) {
        return new ResponseEntity<>(userService.getJwtToken(model), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserModel model, HttpServletRequest request) {
        User user = userService.register(model, AppUtil.getApiBaseUrl(request));
        return new ResponseEntity<>(Map.of("message", "Verification link sent to " + user.getEmail()), HttpStatus.OK);
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<?> verifyRegistration(@RequestParam String token) {
        if (!userService.isValidVerificationToken(token)) {
            return new ResponseEntity<>(Map.of("message", "User verification failed"), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(Map.of("message", "User verified successfully"), HttpStatus.OK);
        }
    }

    @GetMapping("/resendVerifyToken")
    public ResponseEntity<?> resendVerificationToken(@RequestParam String token, HttpServletRequest request) {
        VerificationToken verificationToken = userService.resendVerificationToken(token, AppUtil.getApiBaseUrl(request));
        return new ResponseEntity<>(Map.of("message", "Link sent to " + verificationToken.getUser().getEmail()), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        PasswordResetToken token = userService.sendPasswordResetToken(passwordModel, AppUtil.getApiBaseUrl(request));
        return new ResponseEntity<>(Map.of("message", "Link sent to " + token.getUser().getEmail()), HttpStatus.OK);
    }

    @PostMapping("/savePassword")
    public ResponseEntity<?> savePassword(@RequestParam String token, @RequestBody PasswordModel passwordModel) {
        if (!userService.isValidatePasswordResetToken(token)) throw new BadRequestApiException("Invalid token");

        User user = userService.getUserByPasswordResetToken(token).orElseThrow(() -> new BadRequestApiException("Invalid token"));
        userService.changePassword(user, passwordModel.getNewPassword());

        return new ResponseEntity<>(Map.of("message", "password reset"), HttpStatus.ACCEPTED);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail()).orElseThrow(() -> new NotFoundApiException("Invalid email"));

        if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())) {
            throw new NotFoundApiException("Invalid old password");
        }

        userService.changePassword(user, passwordModel.getNewPassword());

        return new ResponseEntity<>(Map.of("message", "password changed"), HttpStatus.ACCEPTED);
    }
}
