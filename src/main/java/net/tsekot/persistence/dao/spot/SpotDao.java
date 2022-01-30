package net.tsekot.persistence.dao.spot;

import net.tsekot.persistence.entity.Spot;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpotDao {

    private DataSource dataSource;

    public SpotDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Spot> getAll() throws SQLException {
        List<Spot> res = new ArrayList<>();

        Connection connection = dataSource.getConnection();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT id, spot_id, spot_type, available from spots");

            while (resultSet.next()) {
                Spot spot = getSpot(resultSet);
                res.add(spot);
            }

            return res;
        }
    }

    public Optional<Spot> getSpotById(String spotId) throws SQLException {

        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, spot_id, spot_type, available from spots where spot_id = ?")) {
            preparedStatement.setString(1, spotId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            } else {
                return Optional.of(getSpot(resultSet));
            }
        }
    }

    public boolean save(Spot spot) throws SQLException {
        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE spots SET available = ? where spot_id = ?")) {

            preparedStatement.setInt(1, spot.isAvailable() ? 0 : 1);
            preparedStatement.setLong(2, spot.getSpotId());
            int i = preparedStatement.executeUpdate();

            return i == 1;
        }
    }

    private Spot getSpot(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int spot_id = resultSet.getInt("spot_id");
        int spot_type = resultSet.getInt("spot_type");
        int available = resultSet.getInt("available");
        return new Spot(id, spot_id, spot_type, available == 0);
    }
}
