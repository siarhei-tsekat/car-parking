package net.tsekot.persistence.dao.reservation;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationDao {

    private DataSource dataSource;

    public ReservationDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String reserveSpot(long spotId, LocalDateTime startTime, LocalDateTime endTime, String userId) throws SQLException {
        String reservationId = UUID.randomUUID().toString();
        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection
                .prepareStatement("INSERT INTO reservations (reservation_id, user_id, spot_id, start_time, end_time, price) VALUES (?,?,?,?,?,?)")) {

            preparedStatement.setString(1, reservationId);
            preparedStatement.setString(2, userId);
            preparedStatement.setLong(3, spotId);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(startTime));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(endTime));
            preparedStatement.setBigDecimal(6, new BigDecimal("2.0")); // TODO: change hardcode

            int i = preparedStatement.executeUpdate();
            if (i == 1) {
                return reservationId;
            } else {
                throw new SQLException("Spot wasn't inserted");
            }
        }
    }
}
