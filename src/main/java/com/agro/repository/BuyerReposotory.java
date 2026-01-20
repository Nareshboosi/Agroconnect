package com.agro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agro.entity.Buyer;

public interface BuyerReposotory extends JpaRepository<Buyer, Long> {

}
