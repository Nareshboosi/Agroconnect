package com.agro.dto;

import java.time.LocalDate;

public class MarketPriceResponse {

    private Long id;
    private Long cropId;
    private String cropName;
    private String marketName;
    private Double pricePerQuintal;
    private LocalDate date;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCropId() {
		return cropId;
	}
	public void setCropId(Long cropId) {
		this.cropId = cropId;
	}
	public String getCropName() {
		return cropName;
	}
	public void setCropName(String cropName) {
		this.cropName = cropName;
	}
	public String getMarketName() {
		return marketName;
	}
	public void setMarketName(String marketName) {
		this.marketName = marketName;
	}
	public Double getPricePerQuintal() {
		return pricePerQuintal;
	}
	public void setPricePerQuintal(Double pricePerQuintal) {
		this.pricePerQuintal = pricePerQuintal;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public MarketPriceResponse(Long id, Long cropId, String cropName, String marketName, Double pricePerQuintal,
			LocalDate date) {
		super();
		this.id = id;
		this.cropId = cropId;
		this.cropName = cropName;
		this.marketName = marketName;
		this.pricePerQuintal = pricePerQuintal;
		this.date = date;
	}
	public MarketPriceResponse() {
		super();
	}

    // getters & setters
    
}
