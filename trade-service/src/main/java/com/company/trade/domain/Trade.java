package com.company.trade.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String commodity;

    @Enumerated(EnumType.STRING)
    private TradeSide side;

    private BigDecimal quantity;
    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    private LocalDateTime tradeTime;
}