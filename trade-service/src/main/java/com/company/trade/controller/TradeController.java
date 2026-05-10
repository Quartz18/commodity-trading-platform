package com.company.trade.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.trade.domain.Trade;
import com.company.trade.dto.PnlResponse;
import com.company.trade.dto.TradeRequest;
import com.company.trade.service.PositionService;
import com.company.trade.service.TradeService;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return tradeService.createTrade(tradeRequest);
    }
    @GetMapping("/pnl/{commodity}")
    public PnlResponse getPnL(@PathVariable String commodity) {
        BigDecimal marketPrice = new BigDecimal("120");
        return positionService.getPnl(commodity, marketPrice);
    }
}
