package net.tsekot.service;

import net.tsekot.domain.PaymentUserDetails;
import net.tsekot.persistence.C3PODataSource;
import net.tsekot.persistence.TransactionManagerImpl;
import net.tsekot.persistence.dao.user.UserDao;
import net.tsekot.persistence.dao.user.UserNotFoundException;
import net.tsekot.persistence.entity.User;
import org.apache.log4j.Logger;

public class UserService {

    private final static Logger logger = Logger.getLogger(UserService.class);

    private TransactionManagerImpl transactionManagerImpl;
    private UserDao userDao;

    public UserService() {
        this.transactionManagerImpl = new TransactionManagerImpl(C3PODataSource.getC3PODataSource());
        this.userDao = new UserDao(transactionManagerImpl);
    }

    public User getUserByUserName(String username) throws UserNotFoundException {
        try {
            return transactionManagerImpl.execute(() -> userDao.getUserByUserName(username));
        } catch (Exception e) {
            logger.error("Reading user by name threw an exception: " + e);
            throw new UserNotFoundException(e.getMessage());
        }
    }

    public PaymentUserDetails getUserPaymentDetails(String userId) throws UserNotFoundException {
        try {
            User user = transactionManagerImpl.execute(() -> userDao.getUserByUserId(userId));
            return new PaymentUserDetails(user.getUserName(), "fake_email@gmail.com");

        } catch (Exception e) {
            logger.error("Reading user by id threw an exception: " + e);
            throw new UserNotFoundException(e.getMessage());
        }
    }
}
