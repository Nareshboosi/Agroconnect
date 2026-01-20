package com.agro.controller;

import com.agro.dto.AuthResponse;
import com.agro.dto.LoginRequest;
import com.agro.dto.LoginResponse;
import com.agro.dto.RegisterRequest;
import com.agro.entity.Buyer;
import com.agro.entity.Farmer;
import com.agro.entity.User;
import com.agro.enums.Role;
import com.agro.repository.BuyerReposotory;
import com.agro.repository.FarmerRepository;
import com.agro.repository.UserRepository;
import com.agro.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    
    private final PasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BuyerReposotory buyerRepo;
    
    private final FarmerRepository farmerRepository;
    
    @Autowired
    private UserDetailsService userDetailsService;
   

    // ✅ CONSTRUCTOR INJECTION (NO LOMBOK)
    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder,
                          UserRepository userRepo,
                          BuyerReposotory buyerRepo,
                          FarmerRepository farmerRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;   // ✅ NO new keyword
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.buyerRepo = buyerRepo;
        this.farmerRepository = farmerRepository; 
    }

    
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        // 1️⃣ Authenticate user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // 2️⃣ Load UserDetails (IMPORTANT)
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        // 3️⃣ Generate JWT token
        String token = jwtService.generateToken(userDetails);

        // 4️⃣ Get role from User table
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5️⃣ Response
        LoginResponse response = new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }




    // ======================================================
    // REGISTER FARMER
    // ======================================================
    @PostMapping("/register/farmer")
    public ResponseEntity<?> registerFarmer(@RequestBody @Valid RegisterRequest request) {

        // 1️⃣ Prevent duplicate email
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // 2️⃣ AUTH TABLE (LOGIN)
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.FARMER);
        userRepo.save(user);

        // 3️⃣ FARMER PROFILE TABLE
        Farmer farmer = new Farmer();
        farmer.setName(request.getName());
        farmer.setEmail(request.getEmail());
        farmer.setPassword(user.getPassword());   // ✅ REQUIRED
        farmer.setPhone(request.getPhone());
        farmer.setAddress(request.getAddress());  // ✅ REQUIRED
        farmer.setRole(Role.FARMER);               // ✅ REQUIRED

        farmerRepository.save(farmer);

        return ResponseEntity.ok("Farmer registered successfully");
    }


    // ======================================================
    // REGISTER ADMIN
    // ======================================================

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid RegisterRequest request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // AUTH TABLE
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        userRepo.save(user);

        // PROFILE TABLE
        Farmer admin = new Farmer();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(user.getPassword());
        admin.setPhone(request.getPhone());
        admin.setAddress(request.getAddress());
        admin.setRole(Role.ADMIN);
        farmerRepository.save(admin);

        return ResponseEntity.ok("Admin registered successfully");
    }


    
    @PostMapping("/register/buyer")
    public ResponseEntity<?> registerBuyer(@RequestBody RegisterRequest request) {

        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // AUTH TABLE
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.BUYER);
        userRepo.save(user);

        // BUYER PROFILE TABLE (optional)
        Buyer buyer = new Buyer();
        buyer.setName(request.getName());
        buyer.setEmail(request.getEmail());
        buyerRepo.save(buyer);

        return ResponseEntity.ok("Buyer registered successfully");
    }




}
