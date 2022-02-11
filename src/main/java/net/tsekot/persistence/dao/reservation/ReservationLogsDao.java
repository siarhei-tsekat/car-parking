package net.tsekot.persistence.dao.reservation;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ReservationLogsDao {

    private final DataSource dataSource;

    public ReservationLogsDao(DataSource transactionManager) {
        dataSource = transactionManager;
    }

    public void write(String reservationId, String userId, String startTime, String endTime, String totalCost, BigDecimal price) throws SQLException {
        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO reservation_logs (reservation_id, user_id, spot_id, start_time, end_time, price, cost) values (?,?,?,?,?,?,?)")) {
            preparedStatement.setString(1, reservationId);
            preparedStatement.setString(2, userId);
            preparedStatement.setInt(3, 1);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(startTime));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(endTime));
            preparedStatement.setBigDecimal(6, price);
            preparedStatement.setBigDecimal(7, new BigDecimal(totalCost));

            preparedStatement.execute();
        }
    }
}
