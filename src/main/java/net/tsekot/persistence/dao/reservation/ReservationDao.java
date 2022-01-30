package net.tsekot.persistence.dao.reservation;

import net.tsekot.persistence.entity.Reservation;
import net.tsekot.persistence.entity.Spot;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public List<Reservation> getReservationsByUser(String userId) throws SQLException {
        List<Reservation> res = new ArrayList<>();

        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, reservation_id, user_id, spot_id, start_time, end_time, price from reservations WHERE user_id = ?")) {
            preparedStatement.setString(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                long id = resultSet.getLong("id");
                String reservation_id = resultSet.getString("reservation_id");
                String user_id = resultSet.getString("user_id");
                String spot_id = resultSet.getString("spot_id");
                Timestamp start_time = resultSet.getTimestamp("start_time");
                Timestamp end_time = resultSet.getTimestamp("end_time");
                BigDecimal price = resultSet.getBigDecimal("price");
                res.add(new Reservation(id, reservation_id, user_id, spot_id, start_time.toLocalDateTime(), end_time.toLocalDateTime(), price));
            }

            return res;
        }
    }

    public boolean deleteReservation(String userId, String reservationId) throws SQLException {
        Connection connection = dataSource.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM reservations WHERE reservation_id = ? and user_id = ?")) {
            preparedStatement.setString(1, reservationId);
            preparedStatement.setString(2, userId);
            int i = preparedStatement.executeUpdate();
            return i == 1;
        }
    }

    public Optional<Reservation> getByReservationById(String reservationId) throws SQLException {
        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, reservation_id, user_id, spot_id, start_time, end_time, price from reservations WHERE reservation_id = ?")) {
            preparedStatement.setString(1, reservationId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String reservation_id = resultSet.getString("reservation_id");
                String user_id = resultSet.getString("user_id");
                String spot_id = resultSet.getString("spot_id");
                Timestamp start_time = resultSet.getTimestamp("start_time");
                Timestamp end_time = resultSet.getTimestamp("end_time");
                BigDecimal price = resultSet.getBigDecimal("price");
                return Optional.of(new Reservation(id, reservation_id, user_id, spot_id, start_time.toLocalDateTime(), end_time.toLocalDateTime(), price));
            }

            return Optional.empty();
        }
    }
}
