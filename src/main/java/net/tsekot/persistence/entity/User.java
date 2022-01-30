package net.tsekot.persistence.entity;

public class User {
    private long id;
    private final String userName;
    private final String password;

    public User(long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
