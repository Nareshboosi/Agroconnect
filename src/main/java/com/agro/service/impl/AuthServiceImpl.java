package com.agro.service.impl;

import com.agro.dto.LoginRequest;
import com.agro.dto.RegisterRequest;
import com.agro.entity.Farmer;
import com.agro.enums.Role;
import com.agro.repository.FarmerRepository;
import com.agro.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private FarmerRepository farmerRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public Farmer register(RegisterRequest request) {

	    if (farmerRepository.existsByEmail(request.getEmail())) {
	        throw new RuntimeException("Email already registered");
	    }

	    Farmer farmer = new Farmer();
	    farmer.setName(request.getName());
	    farmer.setEmail(request.getEmail());
	    farmer.setPhone(request.getPhone());
	    farmer.setAddress(request.getAddress());
	    farmer.setPassword(passwordEncoder.encode(request.getPassword()));
	    farmer.setRole(Role.FARMER);   // ✅ REQUIRED

	    return farmerRepository.save(farmer);
	}


	public Farmer login(LoginRequest request) {

		Farmer farmer = farmerRepository
		        .findByEmail(request.getEmail())
		        .orElseThrow(() -> new RuntimeException("Farmer not found"));

	    if (farmer != null &&
	        passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
	        return farmer;
	    }
	    return null;
	}

	

}
