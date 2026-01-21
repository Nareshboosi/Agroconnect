package com.agro.controller;

import com.agro.dto.CropRequest;
import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import com.agro.repository.CropRepository;
import com.agro.repository.FarmerRepository;
import com.agro.service.CropService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crops")
@CrossOrigin(origins = "http://localhost:5173")
public class CropController {

    private final CropService cropService;
    
    @Autowired
    private CropRepository cropRepo;
    @Autowired
    private FarmerRepository farmerRepository;
    

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    // =========================
    // FARMER - ADD CROP
    // =========================
    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/add")
    public ResponseEntity<Crop> addCrop(
            @RequestBody CropRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                cropService.addCrop(request, authentication.getName()));
    }

    // =========================
    // FARMER - MY CROPS
    // =========================
    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/my-crops")
    public ResponseEntity<List<Crop>> getMyCrops(Authentication authentication) {
        return ResponseEntity.ok(
                cropService.getMyCrops(authentication.getName()));
    }

    // =========================
    // ADMIN - ALL CROPS
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    @GetMapping("/all")
    public ResponseEntity<List<Crop>> getAllCrops() {
        return ResponseEntity.ok(cropService.getAllCrops());
    }

    // =========================
    // FARMER - UPDATE OWN CROP
    // =========================
    @PutMapping("/my-crops/{id}")
    public Crop updateMyCrop(
            @PathVariable Long id,
            @RequestBody Crop updated,
            Authentication auth
    ) {
        String email = auth.getName();

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Crop crop = cropRepo.findByIdAndFarmer(id, farmer)
                .orElseThrow(() -> new RuntimeException("Crop not found or access denied"));

        crop.setCropName(updated.getCropName());
        crop.setCropType(updated.getCropType());
        crop.setAvailableQuantity(updated.getAvailableQuantity());
        crop.setPrice(updated.getPrice());
        crop.setSeason(updated.getSeason());

        return cropRepo.save(crop);
    }


    // =========================
    // FARMER / ADMIN - DELETE
    // =========================
    @PreAuthorize("hasAnyRole('ADMIN','FARMER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCrop(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        cropService.deleteCrop(id, authentication.getName(), isAdmin);
        return ResponseEntity.ok("Crop deleted successfully");
    }

    // =========================
    // ADMIN - GET CROP BY ID
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Crop> getCropById(@PathVariable Long id) {
        return ResponseEntity.ok(cropService.getCropById(id));
    }
    
    
    @GetMapping("/browse")
    public List<Crop> browseCrops() {
        return cropRepo.findByAvailableQuantityGreaterThan(0);
    }
    @GetMapping("/my-crops/{id}")
    public Crop getMyCropById(
            @PathVariable Long id,
            Authentication auth
    ) {
        String email = auth.getName();

        Farmer farmer = farmerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        return cropRepo.findByIdAndFarmer(id, farmer)
                .orElseThrow(() -> new RuntimeException("Crop not found or access denied"));
    }

}

