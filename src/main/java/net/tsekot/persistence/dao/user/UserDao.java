package net.tsekot.persistence.dao.user;

import net.tsekot.persistence.entity.User;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private final DataSource dataSource;

    public UserDao(DataSource connection) {
        this.dataSource = connection;
    }

    public User getUserByUserName(String username) throws SQLException, UserNotFoundException {

        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement("SELECT id, user_name, password from users where user_name = ?");
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String user_name = resultSet.getString("user_name");
            String password = resultSet.getString("password");
            return new User(id, user_name, password);
        }

        throw new UserNotFoundException("User with username: " + username + " wasn't found");
    }
}
