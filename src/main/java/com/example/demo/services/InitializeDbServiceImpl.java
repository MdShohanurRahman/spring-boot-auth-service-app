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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InitializeDbServiceImpl implements InitializeDbService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
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
        user.setEnabled(true);

        userRepository.save(user);
    }
}
