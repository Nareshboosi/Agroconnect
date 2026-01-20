package com.agro.service;

import java.util.List;
import java.util.Map;

import com.agro.dto.CropRequest;
import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import com.agro.repository.CropRepository;
import com.agro.repository.FarmerRepository;

public interface CropService {
	 Crop addCrop(CropRequest request, String email);

	    List<Crop> getMyCrops(String email);

	    List<Crop> getAllCrops();

	    Crop updateCrop(Long id, Crop crop, String email);

	    void deleteCrop(Long id, String email, boolean isAdmin);

	    Crop getCropById(Long id);

}
