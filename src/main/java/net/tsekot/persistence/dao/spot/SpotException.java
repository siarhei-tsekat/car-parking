package net.tsekot.persistence.dao.spot;

import net.tsekot.domain.DomainException;

public class SpotException extends DomainException {
    public SpotException(String message) {
        super(message);
    }
}
