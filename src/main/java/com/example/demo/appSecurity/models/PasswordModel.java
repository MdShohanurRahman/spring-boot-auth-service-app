/**
 * Created By shoh@n
 * Date: 8/3/2022
 */

package com.example.demo.appSecurity.models;

import lombok.Data;

@Data
public class PasswordModel {

    private String email;
    private String oldPassword;
    private String newPassword;
}
