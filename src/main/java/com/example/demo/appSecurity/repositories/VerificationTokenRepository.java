/**
 * Created By shoh@n
 * Date: 8/20/2022
 */

package com.example.demo.appSecurity.repositories;

import com.example.demo.appSecurity.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
