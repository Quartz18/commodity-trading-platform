package com.company.trade.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeStatus;
import com.company.trade.dto.TradeRequest;
import com.company.trade.repository.TradeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public Trade createTrade(TradeRequest request) {
        Trade trade = Trade.builder()
        .commodity(request.getCommodity())
        .side(request.getSide())
        .quantity(request.getQuantity())
        .price(request.getPrice())
        .status(TradeStatus.NEW)
        .tradeTime(LocalDateTime.now())
        .build();
        return tradeRepository.save(trade);
    }
}
