package net.tsekot.persistence.entity;

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
}
