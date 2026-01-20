package com.agro.service.impl;

import com.agro.entity.Farmer;
import com.agro.repository.FarmerRepository;
import com.agro.service.FarmerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FarmerServiceImpl implements FarmerService {

	@Autowired
	private FarmerRepository farmerRepository;

	@Override
	public Farmer getFarmerById(Long id) {
		return farmerRepository.findById(id).orElse(null);
	}

	@Override
	public List<Farmer> getAllFarmers() {
		return farmerRepository.findAll();
	}

	public Farmer findByEmail(String email) {
		return farmerRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Farmer not found"));
	}
}
