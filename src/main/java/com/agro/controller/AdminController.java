package com.agro.controller;

import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import com.agro.entity.User;
import com.agro.repository.CropRepository;
import com.agro.repository.UserRepository;
import com.agro.service.CropService;
import com.agro.service.FarmerService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final FarmerService farmerService;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    public AdminController(FarmerService farmerService,
                           UserRepository userRepository,
                           CropRepository cropRepository) {
        this.farmerService = farmerService;
        this.userRepository = userRepository;
        this.cropRepository=cropRepository;
    }

    // GET ALL USERS
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/crops")
    public List<Crop> getAllCrops() {
    	return cropRepository.findAll();
    }

    // DELETE USER
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
   
}

