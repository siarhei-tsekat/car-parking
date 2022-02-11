package net.tsekot.persistence.dao.reservation;

import net.tsekot.domain.DomainException;

public class ReservationException extends DomainException {

    public ReservationException(String message) {
        super(message);
    }
}
