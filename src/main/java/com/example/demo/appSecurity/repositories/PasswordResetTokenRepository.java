/**
 * Created By shoh@n
 * Date: 8/21/2022
 */

package com.example.demo.appSecurity.repositories;

import com.example.demo.appSecurity.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
