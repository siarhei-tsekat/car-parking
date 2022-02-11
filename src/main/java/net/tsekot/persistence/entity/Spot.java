package net.tsekot.persistence.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Spot {

    private long id;
    private long spotId;
    private int spotType;
    private boolean available;

    public Spot(long id, long spotId, int spotType, boolean available) {
        this.id = id;
        this.spotId = spotId;
        this.spotType = spotType;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public long getSpotId() {
        return spotId;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getSpotType() {
        return spotType;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "id=" + id +
                ", spotId=" + spotId +
                ", spotType=" + spotType +
                ", available=" + available +
                '}';
    }

    public static Spot extract(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int spot_id = resultSet.getInt("spot_id");
        int spot_type = resultSet.getInt("spot_type");
        int available = resultSet.getInt("available");
        return new Spot(id, spot_id, spot_type, available == 0);
    }
}
