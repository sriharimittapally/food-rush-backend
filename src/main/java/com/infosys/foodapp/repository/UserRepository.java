package com.infosys.foodapp.repository;

import com.infosys.foodapp.entity.User;
import com.infosys.foodapp.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // For login — Spring Security will use this
    Optional<User> findByEmail(String email);

    // Check duplicate registration
    boolean existsByEmail(String email);

    // Check duplicate phone
    boolean existsByPhone(String phone);

    // Get all users by role (Admin use)
    List<User> findByRole(Role role);
}