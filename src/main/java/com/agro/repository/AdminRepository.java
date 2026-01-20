package com.agro.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.agro.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByEmail(String email);
}
