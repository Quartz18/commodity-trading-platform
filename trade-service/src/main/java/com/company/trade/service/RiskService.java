package com.company.trade.service;

import com.company.trade.domain.Position;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.exception.RiskLimitExceededException;
import com.company.trade.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final RiskLimitService riskLimitService;
    private final PositionRepository positionRepository;
    private static final Logger log =
        LoggerFactory.getLogger(TradeService.class);
    public void validateTrade(Trade trade) {

        BigDecimal currentQty = getExistingQuantity(trade.getCommodity());
        BigDecimal maxLimit = riskLimitService.getMaxLimit(trade.getCommodity());
        BigDecimal exposure = getExposure(trade, currentQty);
        if (exposure.compareTo(maxLimit) > 0) {
            throw new RiskLimitExceededException("Risk limit exceeded for commodity: " + trade.getCommodity());
        }
        log.info(
            "Risk validation passed for commodity={}",
            trade.getCommodity()
);
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