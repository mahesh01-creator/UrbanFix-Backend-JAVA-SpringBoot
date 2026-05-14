package com.smartCity.complaintSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartCity.complaintSystem.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
}
