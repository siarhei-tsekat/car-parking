package net.tsekot.persistence.dao.user;

import net.tsekot.domain.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
