package com.company.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.company.trade.domain.RiskLimit;

public interface RiskLimitRepository extends JpaRepository<RiskLimit, Long> {

    Optional<RiskLimit> findByCommodity(String commodity);
}