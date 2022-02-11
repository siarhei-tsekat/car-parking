package net.tsekot.config;

import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties applicationProperties;
    private Properties properties;

    public static ApplicationProperties instance() {
        if (applicationProperties == null) {
            applicationProperties = new ApplicationProperties();
        }
        return applicationProperties;
    }

    private ApplicationProperties() {
        properties = new Properties();
        try {
            properties.load(ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getDbUserName() {
        return (String) properties.get("db.user");
    }

    public String getDbPassword() {
        return (String) properties.get("db.password");
    }

    public String getJdbcUrl() {
        return (String) properties.get("db.jdbcUrl");
    }

    public String getDriverClass() {
        return (String) properties.get("db.driverClass");
    }

    public String getSecret() {
        return (String) properties.get("secret");
    }

    public int getTokenExpirationHours() {
        return (Integer) properties.get("token.expirationHours");
    }
}
