package com.company.trade.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.company.trade.domain.Position;
import com.company.trade.domain.Trade;
import com.company.trade.domain.TradeSide;
import com.company.trade.dto.PnlResponse;
import com.company.trade.exception.ResourceNotFoundException;
import com.company.trade.repository.PositionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;
    private static final Logger log =
        LoggerFactory.getLogger(TradeService.class);

    public Position updatePosition(Trade trade){
        Position position = getPosition(trade.getCommodity());
        BigDecimal tradeQuantity = getTradeQuantity(trade.getQuantity(),trade.getSide());
        BigDecimal tradePrice = trade.getPrice();
        BigDecimal newQuantity = position.getQuantity().add(tradeQuantity);
        if(position.getQuantity().signum() * tradeQuantity.signum() < 0){
            handleOppositeDirection(position, tradeQuantity, tradePrice, newQuantity);
        }
        else{
            handleSameDirection(position, tradeQuantity, tradePrice, newQuantity);
        }
        positionRepository.save(position);
        log.info(
            "Position updated for commodity={} quantity={} avgPrice={}",
            position.getCommodity(),
            position.getQuantity(),
            position.getAveragePrice());
        return position;
    }

    private void handleOppositeDirection(Position position, BigDecimal tradeQuantity, BigDecimal tradePrice, BigDecimal newQuantity) {
        BigDecimal closedQuantity = position.getQuantity().abs().min(tradeQuantity.abs());
        BigDecimal realized = calculateRealizedPnl(position,tradePrice,closedQuantity);
        position.setRealizedPnl(position.getRealizedPnl().add(realized));
        if(newQuantity.compareTo(BigDecimal.ZERO) ==0){
            handleFull(position, tradeQuantity, tradePrice, newQuantity);
        }
        else{
            if(tradeQuantity.abs().compareTo(position.getQuantity().abs()) > 0){
                handleFlip(position, tradeQuantity, tradePrice);
            }
            else{
                handlePartial(position, newQuantity);
            }
        }
    }

    private void handleSameDirection(Position position, BigDecimal tradeQuantity, BigDecimal tradePrice, BigDecimal newQuantity) {
        BigDecimal totalValue = position.getQuantity().multiply(position.getAveragePrice())
                    .add(tradeQuantity.multiply(tradePrice));
        BigDecimal newAvgPrice = totalValue.divide(newQuantity, RoundingMode.HALF_UP);
        position.setQuantity(newQuantity);
        position.setAveragePrice(newAvgPrice);
    }

    private void handleFlip(Position position, BigDecimal tradeQuantity, BigDecimal tradePrice) {
        BigDecimal remainingQuantity = tradeQuantity.abs().subtract(position.getQuantity().abs());
        remainingQuantity = (tradeQuantity.compareTo(BigDecimal.ZERO) < 0) ? remainingQuantity.negate() : remainingQuantity;
        position.setQuantity(remainingQuantity);
        position.setAveragePrice(tradePrice);
    }

    private void handleFull(Position position, BigDecimal tradeQuantity, BigDecimal tradePrice, BigDecimal newQuantity){
        position.setQuantity(BigDecimal.ZERO);
        position.setAveragePrice(BigDecimal.ZERO);
    }
    private void handlePartial(Position position, BigDecimal newQuantity){
        position.setQuantity(newQuantity);
    }
    private BigDecimal calculateRealizedPnl(Position position, BigDecimal tradePrice, BigDecimal closingQuantity) 
    {
        BigDecimal averagePrice = position.getAveragePrice();
        log.debug(
            "Calculating realized PnL: avgPrice={} tradePrice={} qty={}",
            averagePrice,
            tradePrice,
            closingQuantity
        );
        // LONG → SELL
        if (position.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            return tradePrice
                    .subtract(averagePrice)
                    .multiply(closingQuantity);
        }

        // SHORT → BUY
        return averagePrice
                .subtract(tradePrice)
                .multiply(closingQuantity);
    }
    private Position getPosition(String commodity){
        return positionRepository
        .findByCommodity(commodity)
        .orElse(Position.builder()
            .commodity(commodity)
            .quantity(BigDecimal.ZERO)
            .averagePrice(BigDecimal.ZERO)
            .realizedPnl(BigDecimal.ZERO)
            .build());
    }

    private BigDecimal getTradeQuantity(BigDecimal quantity, TradeSide side){
        if (side.equals(TradeSide.SELL)) {
            return quantity.negate();
        }
        return quantity;
    }
    public BigDecimal calculateUnrealizedPnl(Position position, BigDecimal marketPrice) {

        if (position.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal qty = position.getQuantity().abs();

        if (position.getQuantity().signum() == 1) {
            // LONG
            return marketPrice
                    .subtract(position.getAveragePrice())
                    .multiply(qty);
        } else {
            // SHORT
            return position.getAveragePrice()
                    .subtract(marketPrice)
                    .multiply(qty);
        }
    }
    public PnlResponse getPnl(String commodity, BigDecimal marketPrice) {

        Position position = positionRepository.findByCommodity(commodity)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found for commodity: "+commodity));

        BigDecimal unrealized = calculateUnrealizedPnl(position, marketPrice);

        return new PnlResponse(
                commodity,
                position.getQuantity(),
                position.getAveragePrice(),
                position.getRealizedPnl(),
                unrealized
        );
    }
}