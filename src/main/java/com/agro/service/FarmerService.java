package com.agro.service;

import java.util.List;

import com.agro.entity.Farmer;

public interface FarmerService {
	 Farmer getFarmerById(Long id);

	    List<Farmer> getAllFarmers();
	    
	   

}
