package net.tsekot.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class PriceService {

    public Double calculateCost(LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
        long days = ChronoUnit.DAYS.between(startTime, endTime);
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);

        return (days * price.doubleValue()) + (minutes / 60 * price.doubleValue());
    }
}