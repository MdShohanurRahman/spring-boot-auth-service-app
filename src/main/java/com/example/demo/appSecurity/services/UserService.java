/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.services;

import com.example.demo.appSecurity.entities.PasswordResetToken;
import com.example.demo.appSecurity.entities.User;
import com.example.demo.appSecurity.entities.VerificationToken;
import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.PasswordModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;

import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    User register(UserModel model, String baseUrl);

    TokenModel getJwtToken(LoginModel model);

    Boolean isValidVerificationToken(String token);

    VerificationToken resendVerificationToken(String oldToken, String baseUrl);

    PasswordResetToken sendPasswordResetToken(PasswordModel passwordModel, String baseUrl);

    Boolean isValidatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);
}
