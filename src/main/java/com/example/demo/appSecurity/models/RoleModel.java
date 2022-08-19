/**
 * Created By shoh@n
 * Date: 8/18/2022
 */

package com.example.demo.appSecurity.models;

import com.example.demo.appSecurity.enums.RoleName;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleModel {

    private Long id;
    private RoleName name;

    public String getRoleName() {
        return name.name();
    }
}
