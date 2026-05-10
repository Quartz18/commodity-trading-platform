package com.company.trade.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.trade.domain.RiskLimit;
import com.company.trade.exception.RiskLimitExceededException;
import com.company.trade.repository.RiskLimitRepository;

@Service
public class RiskLimitService {

    @Autowired
    private RiskLimitRepository repository;

    public BigDecimal getMaxLimit(String commodity) {
        return repository.findByCommodity(commodity)
                .map(RiskLimit::getMaxPosition)
                .orElseThrow(() -> new RiskLimitExceededException("Risk limit not found"));
    }
}