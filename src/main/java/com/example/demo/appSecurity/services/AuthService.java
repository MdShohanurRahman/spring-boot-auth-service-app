/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.services;

import com.example.demo.appSecurity.models.LoginModel;
import com.example.demo.appSecurity.models.TokenModel;
import com.example.demo.appSecurity.models.UserModel;

public interface AuthService {

    UserModel register(UserModel model);

    TokenModel getToken(LoginModel model);
}
