package com.agro.entity;

import java.time.LocalDate;
import com.agro.entity.Crop;

import jakarta.persistence.*;

@Entity
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private String marketName;

    @Column(name = "price_per_quintal", nullable = false)
    private Double pricePerQuintal;

    private LocalDate date;

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
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
}
