package net.tsekot.service;

import net.tsekot.persistence.C3PODataSource;
import net.tsekot.persistence.TransactionManager;
import net.tsekot.persistence.TransactionManagerImpl;
import net.tsekot.persistence.UnitOfWork;
import net.tsekot.persistence.dao.spot.SpotDao;
import net.tsekot.persistence.dao.spot.SpotException;
import net.tsekot.persistence.entity.Spot;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SpotService {

    private final static Logger logger = Logger.getLogger(SpotService.class);
    private final TransactionManager transactionManager;
    private final SpotDao spotDao;

    public SpotService() {
        transactionManager = new TransactionManagerImpl(C3PODataSource.getC3PODataSource());
        spotDao = new SpotDao(transactionManager);
    }


    public List<Spot> getAllAvailableSpots() throws SpotException {
        try {
            UnitOfWork<List<Spot>, SQLException> unitOfWork = spotDao::getAll;

            List<Spot> all = transactionManager.execute(unitOfWork);

            return all.stream()
                    .filter(Spot::isAvailable)
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            logger.error(e);
            throw new SpotException(e.getMessage());
        }
    }
}
