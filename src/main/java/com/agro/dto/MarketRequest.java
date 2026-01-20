package com.agro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MarketRequest {


    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotBlank
    private String marketName;

   
    private Double PricePerQuintal;

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public Double getPricePerQuintal() {
        return PricePerQuintal;
    }

    public void setPricePerQuintal(Double price) {
        this.PricePerQuintal = price;
    }
}
