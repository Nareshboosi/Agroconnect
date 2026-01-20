package com.agro.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agro.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {


	Optional<Farmer> findByEmail(String email);

    boolean existsByEmail(String email); 
}

