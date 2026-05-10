package com.company.trade.exception;

public class RiskLimitExceededException extends RuntimeException {

    public RiskLimitExceededException(String message) {
        super(message);
    }
}