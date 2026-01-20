package com.agro.repository;


import com.agro .entity.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketRepository extends JpaRepository<MarketPrice, Long> {

    
    List<MarketPrice> findByCropId(Long id);
}
