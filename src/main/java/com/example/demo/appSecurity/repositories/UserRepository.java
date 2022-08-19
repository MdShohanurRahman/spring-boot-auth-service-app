/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.appSecurity.repositories;

import com.example.demo.appSecurity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
