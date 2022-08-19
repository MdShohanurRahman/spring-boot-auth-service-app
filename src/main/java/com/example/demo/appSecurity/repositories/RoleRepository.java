/**
 * Created By shoh@n
 * Date: 8/13/2022
 */

package com.example.demo.appSecurity.repositories;

import com.example.demo.appSecurity.entities.Role;
import com.example.demo.appSecurity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName roleName);

    boolean existsByName(RoleName name);
}
