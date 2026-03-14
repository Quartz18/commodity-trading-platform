package com.company.trade.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.company.trade.domain.Position;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.repository.PositionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    public Position updatePosition(Trade trade){
        Position position = positionRepository
        .findByCommodity(trade.getCommodity())
        .orElse(Position.builder()
            .commodity(trade.getCommodity())
            .quantity(BigDecimal.ZERO)
            .build());
        BigDecimal quantity = trade.getQuantity();
        if (trade.getSide() == TradeSide.BUY) {
            position.setQuantity(position.getQuantity().add(quantity));
        } else {
            position.setQuantity(position.getQuantity().subtract(quantity));
        }
        return positionRepository.save(position);
    }
}