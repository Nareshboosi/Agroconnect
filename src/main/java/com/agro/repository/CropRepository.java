package com.agro.repository;


import com.agro.entity.Crop;
import com.agro.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {

    
    List<Crop> findByFarmer(Farmer farmer);

    Optional<Crop> findByIdAndFarmer(Long id, Farmer farmer);
    Optional<Crop> findByCropName(String name);

	List<Crop> findByFarmerEmail(String email);
    
	List<Crop> findByFarmerId(Long farmerId);
	List<Crop> findByAvailableQuantityGreaterThan(int qty);
	
	boolean existsByCropName(String CropName);
	
	
	
}
