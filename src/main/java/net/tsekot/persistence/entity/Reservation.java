package net.tsekot.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Reservation {

    private long id;
    private String reservationId;
    private String userId;
    private String spotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;

    public Reservation(long id, String reservationId, String userId, String spotId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price) {
        this.id = id;
        this.reservationId = reservationId;
        this.userId = userId;
        this.spotId = spotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationId='" + reservationId + '\'' +
                ", userId='" + userId + '\'' +
                ", spotId='" + spotId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", price=" + price +
                '}';
    }
}
