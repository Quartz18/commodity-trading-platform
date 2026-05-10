package com.company.trade.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PnlResponse {
    private String commodity;
    private BigDecimal quantity;
    private BigDecimal avgPrice;
    private BigDecimal realizedPnl;
    private BigDecimal unrealizedPnl;
}