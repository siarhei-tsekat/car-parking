package net.tsekot.service;

import net.tsekot.persistence.dao.reservation.ReservationException;

public class ReservationNotFoundException extends ReservationException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
