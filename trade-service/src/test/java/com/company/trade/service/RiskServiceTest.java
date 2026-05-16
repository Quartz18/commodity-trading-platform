package com.company.trade.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.trade.domain.RiskLimit;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.exception.RiskLimitExceededException;
import com.company.trade.repository.PositionRepository;
import com.company.trade.repository.RiskLimitRepository;

@ExtendWith(MockitoExtension.class)
public class RiskServiceTest {

    @Mock
    RiskLimitRepository riskLimitRepository;
    @InjectMocks
    RiskLimitService riskLimitService;
    PositionRepository positionRepository;
    RiskService riskService;
    @BeforeEach
    void setUp() {
        positionRepository = Mockito.mock(PositionRepository.class); 
        riskService = new RiskService(riskLimitService, positionRepository);
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.empty());
    }
    @Test
    void shouldPassRiskValidation() {
        Trade trade = new Trade();
        trade.setSide(TradeSide.BUY);
        trade.setQuantity(new BigDecimal(15));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        Long id = 41L;
        RiskLimit riskLimit = new RiskLimit(id,"GOLD", new BigDecimal(20));
        when(riskLimitRepository.findByCommodity("GOLD")).thenReturn(Optional.of(riskLimit));
        assertDoesNotThrow(() ->
            riskService.validateTrade(trade)
        );
    }
    @Test
    void shouldThrowWhenRiskLimitExceeded() {
        Trade trade = new Trade();
        trade.setSide(TradeSide.BUY);
        trade.setQuantity(new BigDecimal(15));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        Long id = 41L;
        RiskLimit riskLimit = new RiskLimit(id,"GOLD", new BigDecimal(5));
        when(riskLimitRepository.findByCommodity("GOLD")).thenReturn(Optional.of(riskLimit));
        assertThrows(RiskLimitExceededException.class, () ->
            riskService.validateTrade(trade)
        );
    }
}
