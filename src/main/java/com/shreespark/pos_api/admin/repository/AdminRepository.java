package com.shreespark.pos_api.admin.repository;

import com.shreespark.pos_api.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    boolean existsByEmail(String email);

    Optional<Admin> findByEmailAndActiveTrue(String email);
}
