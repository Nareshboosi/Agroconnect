package com.agro.controller;

import com.agro.dto.MarketPriceResponse;
import com.agro.dto.MarketRequest;
import com.agro.entity.MarketPrice;
import com.agro.service.MarketPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market-prices")
@CrossOrigin(origins = "http://localhost:5173")
public class MarketPriceController {

    @Autowired
    private MarketPriceService marketPriceService;

    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public MarketPrice addMarketPrice(@RequestBody MarketRequest request) {
        return marketPriceService.addPrice(request);
    }

    // ADMIN + FARMER + BUYER (READ ONLY)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FARMER','BUYER')")
    public List<MarketPriceResponse> getAllMarketPrices() {
        return marketPriceService.getAllPrices();
    }

    @GetMapping("/crop/{cropId}")
    @PreAuthorize("hasAnyRole('ADMIN','FARMER','BUYER')")
    public List<MarketPrice> getPricesByCrop(@PathVariable Long cropId) {
        return marketPriceService.getPricesByCrop(cropId);
    }

    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public MarketPrice updateMarketPrice(
            @PathVariable Long id,
            @RequestBody MarketRequest updatedPrice) {
        return marketPriceService.updatePrice(id, updatedPrice);
    }

    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteMarketPrice(@PathVariable Long id) {
        marketPriceService.deletePrice(id);
    }
}
