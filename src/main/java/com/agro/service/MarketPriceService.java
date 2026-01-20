package com.agro.service;

import com.agro.dto.MarketPriceResponse;
import com.agro.dto.MarketRequest;
import com.agro.entity.MarketPrice;

import java.util.List;

public interface MarketPriceService {

    MarketPrice addPrice(MarketRequest request);

    MarketPrice updatePrice(Long id, MarketRequest request);

    List<MarketPriceResponse> getAllPrices();

    List<MarketPrice> getPricesByCrop(Long cropId);

    void deletePrice(Long id);

	MarketPrice getById(Long id);
}
