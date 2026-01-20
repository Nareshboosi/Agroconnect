package com.agro.service.impl;

import com.agro.dto.CropRequest;
import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import com.agro.exception.ResourceNotFoundException;
import com.agro.repository.CropRepository;
import com.agro.repository.FarmerRepository;
import com.agro.service.CropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final FarmerRepository farmerRepository;

    public CropServiceImpl(CropRepository cropRepository,
                           FarmerRepository farmerRepository) {
        this.cropRepository = cropRepository;
        this.farmerRepository = farmerRepository;
    }

    // =========================
    // ADD CROP
    // =========================
    @Override
    public Crop addCrop(CropRequest request, String email) {

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Crop crop = new Crop();
        crop.setCropName(request.getCropName());
        crop.setCropType(request.getCropType());
        crop.setQuantity(request.getQuantity());
        crop.setPrice(request.getPrice());
        crop.setSeason(request.getSeason());
        crop.setFarmer(farmer);

        return cropRepository.save(crop);
    }

    // =========================
    // FARMER - MY CROPS
    // =========================
    @Override
    public List<Crop> getMyCrops(String email) {

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return cropRepository.findByFarmerId(farmer.getId());
    }

    // =========================
    // ADMIN - ALL CROPS
    // =========================
    @Override
    public List<Crop> getAllCrops() {
        return cropRepository.findAll();
    }

    // =========================
    // FARMER - UPDATE OWN CROP
    // =========================
    @Override
    public Crop updateCrop(Long id, Crop updatedCrop, String email) {

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (!crop.getFarmer().getId().equals(farmer.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        crop.setCropName(updatedCrop.getCropName());
        crop.setCropType(updatedCrop.getCropType());
        crop.setQuantity(updatedCrop.getQuantity());
        crop.setPrice(updatedCrop.getPrice());
        crop.setSeason(updatedCrop.getSeason());

        return cropRepository.save(crop);
    }

    // =========================
    // DELETE CROP
    // =========================
    @Override
    public void deleteCrop(Long id, String email, boolean isAdmin) {

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (!isAdmin) {
            Farmer farmer = farmerRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));

            if (!crop.getFarmer().getId().equals(farmer.getId())) {
                throw new RuntimeException("Unauthorized");
            }
        }

        cropRepository.delete(crop);
    }

    // =========================
    // ADMIN - GET BY ID
    // =========================
    @Override
    public Crop getCropById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
    }
}





