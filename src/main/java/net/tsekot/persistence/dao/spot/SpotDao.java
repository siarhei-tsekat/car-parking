package net.tsekot.persistence.dao.spot;

import net.tsekot.persistence.entity.Spot;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpotDao {

    private DataSource dataSource;

    public SpotDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Spot> getAll() throws SQLException {
        List<Spot> res = new ArrayList<>();

        Connection connection = dataSource.getConnection();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT id, spot_id, available from spots");

            while (resultSet.next()) {
                Spot spot = getSpot(resultSet);
                res.add(spot);
            }

            return res;
        }
    }

    public Spot getSpotById(Integer spotId) throws SQLException, SpotNotFoundException {

        Connection connection = dataSource.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, spot_id, available from spots where spot_id = ?")) {
            preparedStatement.setInt(1, spotId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SpotNotFoundException("Spot with such id doesn't exist: " + spotId);
            } else {
                return getSpot(resultSet);
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
        int available = resultSet.getInt("available");
        return new Spot(id, spot_id, available == 0);
    }
}
