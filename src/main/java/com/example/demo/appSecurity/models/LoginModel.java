/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginModel {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
