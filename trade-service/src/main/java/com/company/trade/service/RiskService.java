package com.company.trade.service;

import com.company.trade.domain.Position;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.exception.RiskException;
import com.company.trade.repository.PositionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RiskService {

    @Autowired
    private RiskLimitService riskLimitService;
    private final PositionRepository positionRepository;;

    public void validateTrade(Trade trade) {

        BigDecimal currentQty = getExistingQuantity(trade.getCommodity());
        BigDecimal maxLimit = riskLimitService.getMaxLimit(trade.getCommodity());
        BigDecimal exposure = getExposure(trade, currentQty);
        if (exposure.compareTo(maxLimit) > 0) {
            throw new RiskException("Position limit exceeded for commodity: " + trade.getCommodity());
        }
    }

    private BigDecimal getExistingQuantity(String commodity){
        Position position = positionRepository
                .findByCommodity(commodity)
                .orElse(null);
        return position != null? position.getQuantity() : BigDecimal.ZERO;
    }
    
    private BigDecimal getExposure(Trade trade, BigDecimal currentQty){
        if (trade.getSide() == TradeSide.BUY) {
            return currentQty.add(trade.getQuantity()).abs();
        } else {
            return currentQty.subtract(trade.getQuantity()).abs();
        }
    }
}