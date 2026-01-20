package com.agro.service.impl;

import com.agro.dto.MarketPriceResponse;
import com.agro.dto.MarketRequest;
import com.agro.entity.Crop;
import com.agro.entity.MarketPrice;
import com.agro.repository.CropRepository;
import com.agro.repository.MarketRepository;
import com.agro.service.MarketPriceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarketPriceServiceImpl implements MarketPriceService {

    private final MarketRepository marketRepository;
    private final CropRepository cropRepository;

    public MarketPriceServiceImpl(MarketRepository marketRepository,
                                  CropRepository cropRepository) {
        this.marketRepository = marketRepository;
        this.cropRepository = cropRepository;
    }

    // ===============================
    // ADD PRICE
    // ===============================
    @Override
    public MarketPrice addPrice(MarketRequest request) {

        if (request.getCropId() == null) {
            throw new RuntimeException("Crop ID must not be null");
        }

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        MarketPrice mp = new MarketPrice();
        mp.setCrop(crop);  // ⭐ MOST IMPORTANT LINE
        mp.setMarketName(request.getMarketName());
        mp.setPricePerQuintal(request.getPricePerQuintal());
        mp.setDate(LocalDate.now());

        return marketRepository.save(mp);
    }



    // ===============================
    // UPDATE PRICE
    // ===============================
    @Override
    public MarketPrice updatePrice(Long id, MarketRequest request) {

        MarketPrice existing = marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market price not found"));

        Crop crop = cropRepository.findById(request.getCropId())
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        existing.setCrop(crop);
        existing.setMarketName(request.getMarketName());
        existing.setPricePerQuintal(request.getPricePerQuintal());

        return marketRepository.save(existing);
        }

    @Override
    public List<MarketPriceResponse> getAllPrices() {

        return marketRepository.findAll().stream().map(mp -> {
            MarketPriceResponse dto = new MarketPriceResponse();
            dto.setId(mp.getId());
            dto.setCropId(mp.getCrop().getId());
            dto.setCropName(mp.getCrop().getCropName()); // 🔥 FIX
            dto.setMarketName(mp.getMarketName());
            dto.setPricePerQuintal(mp.getPricePerQuintal());
            dto.setDate(mp.getDate());
            return dto;
        }).toList();
    }


    @Override
    public List<MarketPrice> getPricesByCrop(Long cropId) {
        return marketRepository.findByCropId(cropId);
    }

    @Override
    public void deletePrice(Long id) {
        marketRepository.deleteById(id);
    }
    @Override
    public MarketPrice getById(Long id) {
        return marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market price not found"));
    }
}
