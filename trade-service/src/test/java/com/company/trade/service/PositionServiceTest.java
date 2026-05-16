package com.company.trade.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.trade.domain.Position;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.repository.PositionRepository;

@ExtendWith(MockitoExtension.class)
public class PositionServiceTest {
    @Mock
    PositionRepository positionRepository;
    @InjectMocks
    PositionService positionService;
    @Test
    void shouldCreateNewPositionWhenNewTrade() {
        Trade trade = new Trade();
        trade.setSide(TradeSide.BUY);
        trade.setQuantity(new BigDecimal(5));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.empty());
        Position position = positionService.updatePosition(trade);

        assertEquals(new BigDecimal(5), position.getQuantity());
        assertEquals(new BigDecimal(120), position.getAveragePrice());
        assertEquals(BigDecimal.ZERO, position.getRealizedPnl());
    }
    @Test
    void shouldUpdateAveragePriceWhenSameDirectionTrade() {
        Position position = new Position();
        position.setCommodity("GOLD");
        position.setQuantity(new BigDecimal(10));
        position.setAveragePrice(new BigDecimal("100"));
        position.setRealizedPnl(BigDecimal.ZERO);
        Trade trade = new Trade();
        trade.setSide(TradeSide.BUY);
        trade.setQuantity(new BigDecimal(5));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.of(position));
        Position results = positionService.updatePosition(trade);

        assertEquals(new BigDecimal(15), results.getQuantity());
        assertEquals(new BigDecimal(107), position.getAveragePrice());
        assertEquals(BigDecimal.ZERO, position.getRealizedPnl());
    }
    @Test
    void shouldCalculateRealizedPnlForPartialClose() {
        Position position = new Position();
        position.setCommodity("GOLD");
        position.setQuantity(new BigDecimal(10));
        position.setAveragePrice(new BigDecimal("100"));
        position.setRealizedPnl(BigDecimal.ZERO);

        Trade trade = new Trade();
        trade.setSide(TradeSide.SELL);
        trade.setQuantity(new BigDecimal(5));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.of(position));
        Position results = positionService.updatePosition(trade);


        assertEquals(new BigDecimal(5), results.getQuantity());
        assertEquals(new BigDecimal("100"), results.getAveragePrice());
        assertEquals(new BigDecimal("100"), results.getRealizedPnl());
    }
    @Test
    void shouldClosePositionFully() {
        Position position = new Position();
        position.setCommodity("GOLD");
        position.setQuantity(new BigDecimal(10));
        position.setAveragePrice(new BigDecimal("100"));
        position.setRealizedPnl(BigDecimal.ZERO);

        Trade trade = new Trade();
        trade.setSide(TradeSide.SELL);
        trade.setQuantity(new BigDecimal(10));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.of(position));
        Position results = positionService.updatePosition(trade);


        assertEquals(BigDecimal.ZERO, results.getQuantity());
        assertEquals(BigDecimal.ZERO, results.getAveragePrice());
        assertEquals(new BigDecimal(200), results.getRealizedPnl());
    }
    @Test
    void shouldFlipPositionCorrectly() {
        Position position = new Position();
        position.setCommodity("GOLD");
        position.setQuantity(new BigDecimal(10));
        position.setAveragePrice(new BigDecimal("100"));
        position.setRealizedPnl(BigDecimal.ZERO);

        Trade trade = new Trade();
        trade.setSide(TradeSide.SELL);
        trade.setQuantity(new BigDecimal(15));
        trade.setPrice(new BigDecimal("120"));
        trade.setCommodity("GOLD");
        when(positionRepository.findByCommodity("GOLD")).thenReturn(Optional.of(position));
        Position results = positionService.updatePosition(trade);


        assertEquals(new BigDecimal(-5), results.getQuantity());
        assertEquals(new BigDecimal(120), results.getAveragePrice());
        assertEquals(new BigDecimal(200), results.getRealizedPnl());
    }
    @Test
    void shouldCalculateUnrealizedForLong() {
        Position position = new Position();
        position.setQuantity(new BigDecimal(10));
        position.setAveragePrice(new BigDecimal("100"));

        BigDecimal result =
            positionService.calculateUnrealizedPnl(position, new BigDecimal("120"));

        assertEquals(new BigDecimal("200"), result);
    }
    @Test
    void shouldCalculateUnrealizedForShort() {
        Position position = new Position();
        position.setQuantity(new BigDecimal(-10));
        position.setAveragePrice(new BigDecimal(120));

        BigDecimal result =
            positionService.calculateUnrealizedPnl(position, new BigDecimal("100"));

        assertEquals(new BigDecimal("200"), result);
    }
}