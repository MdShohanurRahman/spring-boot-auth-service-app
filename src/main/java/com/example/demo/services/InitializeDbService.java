/**
 * Created By shoh@n
 * Date: 8/18/2022
 */

package com.example.demo.services;

import com.example.demo.appSecurity.entities.Role;
import com.example.demo.appSecurity.entities.User;
import com.example.demo.appSecurity.enums.RoleName;
import com.example.demo.appSecurity.repositories.RoleRepository;
import com.example.demo.appSecurity.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
@Slf4j
public class InitializeDbService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public InitializeDbService(
            UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void init() {
        log.info("importing roles");
        importRoles();

        log.info("import super admin");
        importSuperAdmin();
    }

    private void importRoles() {
        List<Role> roles = new ArrayList<>();

        for (RoleName name: RoleName.values()) {
            if (!roleRepository.existsByName(name)) {
                Role role = new Role();
                role.setName(name);
                roles.add(role);
            }
        }

        roleRepository.saveAll(roles);
    }

    private void importSuperAdmin() {
        if (userRepository.existsByEmail("admin@gmail.com")) return;

        User user = new User();
        Set<Role> roles = new HashSet<>();

        Role admin = roleRepository.findByName(RoleName.ROLE_ADMIN).get();
        roles.add(admin);

        user.setUuid(UUID.randomUUID().toString());
        user.setEmail("admin@gmail.com");
        user.setPassword(passwordEncoder.encode("1234@A"));
        user.setRoles(roles);

        userRepository.save(user);
    }
}
