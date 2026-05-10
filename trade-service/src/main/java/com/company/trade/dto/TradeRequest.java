package com.company.trade.dto;

import java.math.BigDecimal;
import com.company.trade.domain.TradeSide;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TradeRequest {

    @NotNull(message = "Commodity is required")
    @NotBlank(message = "Commodity is required")
    private String commodity;
    @NotNull(message = "Side is required")
    private TradeSide side;
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
}