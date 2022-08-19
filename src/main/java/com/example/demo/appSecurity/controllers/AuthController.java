/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.controllers;

import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;
import com.example.demo.appSecurity.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenModel> token(@Valid @RequestBody LoginModel model) {
        return new ResponseEntity<>(authService.getToken(model), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserModel model) {
        model = authService.register(model);
        return new ResponseEntity<>(model.response(), HttpStatus.CREATED);
    }
}
