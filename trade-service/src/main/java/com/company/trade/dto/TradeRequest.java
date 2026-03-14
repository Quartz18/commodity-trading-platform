package com.company.trade.dto;

import java.math.BigDecimal;
import com.company.trade.domain.TradeSide;
import lombok.Data;

@Data
public class TradeRequest {

    private String commodity;
    private TradeSide side;
    private BigDecimal quantity;
    private BigDecimal price;
}
