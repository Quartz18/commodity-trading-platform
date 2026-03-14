package com.company.trade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.trade.domain.Position;


public interface PositionRepository extends JpaRepository<Position, Long>  {
    Optional<Position> findByCommodity(String commodity);
}