package com.erick.nutricontrol.security.user.repository;

import com.erick.nutricontrol.security.user.Enum.Role;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Page<User> findByIsBannedFalse(Pageable pageable);
    Page<User> findByIsBannedTrue(Pageable pageable);
    Page<User> findByRoleAndIsBannedFalse(Role role, Pageable pageable);
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
