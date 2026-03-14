package com.company.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.trade.domain.Trade;

public interface TradeRepository extends JpaRepository<Trade, Long> {

}