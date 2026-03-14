package com.company.trade.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.trade.domain.Trade;
import com.company.trade.dto.TradeRequest;
import com.company.trade.service.PositionService;
import com.company.trade.service.TradeService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final PositionService positionService;

    @PostMapping
    public Trade createTrade(@RequestBody TradeRequest tradeRequest) {
        Trade trade = tradeService.createTrade(tradeRequest);
        positionService.updatePosition(trade);
        return trade;
    }
}
