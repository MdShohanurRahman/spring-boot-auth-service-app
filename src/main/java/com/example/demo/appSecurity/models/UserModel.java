/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserModel {

    @Email
    private String email;

    @NotBlank
    private String password;

    private Set<RoleModel> roles = new HashSet<>();

    public List<String> getRoleNameList() {
        return roles.stream().map(RoleModel::getRoleName).collect(Collectors.toList());
    }

    public Map<String, Object> response() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("email", email);
        map.put("roles", getRoleNameList());
        return map;
    }
}
