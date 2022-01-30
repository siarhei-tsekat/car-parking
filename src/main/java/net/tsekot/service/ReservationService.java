package net.tsekot.service;

import net.tsekot.persistence.C3PODataSource;
import net.tsekot.persistence.TransactionManager;
import net.tsekot.persistence.TransactionManagerImpl;
import net.tsekot.persistence.UnitOfWork;
import net.tsekot.persistence.dao.reservation.ReservationDao;
import net.tsekot.persistence.dao.reservation.ReservationException;
import net.tsekot.persistence.dao.spot.SpotDao;
import net.tsekot.persistence.entity.Reservation;
import net.tsekot.persistence.entity.Spot;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {

    private final static Logger logger = Logger.getLogger(ReservationService.class);

    private final SpotDao spotDao;
    private final ReservationDao reservationDao;
    private final TransactionManager transactionManager;

    public ReservationService() {
        this.transactionManager = new TransactionManagerImpl(C3PODataSource.getC3PODataSource());
        this.spotDao = new SpotDao(transactionManager);
        this.reservationDao = new ReservationDao(transactionManager);
    }

    public String reserveSpot(String userId, Integer spotId, LocalDateTime startTime, LocalDateTime endTime) throws ReservationException {

        try {
            UnitOfWork<String, Exception> unitOfWork = () -> {
                Spot spotById = spotDao.getSpotById(spotId);

                if (spotById.isAvailable()) {
                    spotById.setAvailable(false);
                    boolean saved = spotDao.save(spotById);

                    if (saved) {
                        return reservationDao.reserveSpot(spotById.getSpotId(), startTime, endTime, userId);
                    } else {
                        throw new ReservationException("Spot with id " + spotId + " wasn't changed.");
                    }
                } else {
                    throw new ReservationException("Spot with id " + spotId + " is not available for reservation.");
                }
            };

            return transactionManager.execute(unitOfWork, Connection.TRANSACTION_REPEATABLE_READ);

        } catch (Exception e) {
            logger.error(e);
            throw new ReservationException(e.getMessage());
        }
    }

    public List<Reservation> getReservationsByUser(String userId) throws ReservationException {
        try {
            return transactionManager.execute(() -> reservationDao.getReservationsByUser(userId), Connection.TRANSACTION_READ_COMMITTED);
        } catch (Exception e) {
            logger.error(e);
            throw new ReservationException(e.getMessage());
        }
    }

    public boolean cancelReservation(String userId, String reservationId) throws ReservationException {
        try {

            UnitOfWork<Boolean, ReservationNotFoundException> unitOfWork = () -> {
                if (reservationDao.getByReservationById(reservationId).isPresent()) {
                    return reservationDao.deleteReservation(userId, reservationId);
                } else {
                    throw new ReservationNotFoundException("Reservation with id: " + reservationId + " doesn't exist");
                }
            };

            return transactionManager.execute(unitOfWork, Connection.TRANSACTION_REPEATABLE_READ);
        } catch (SQLException e) {
            logger.error(e);
            throw new ReservationException(e.getMessage());
        }
    }
}
