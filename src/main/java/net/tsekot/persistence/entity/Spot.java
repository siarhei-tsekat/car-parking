package net.tsekot.persistence.entity;

public class Spot {

    private long id;

    private long spotId;

    private boolean available;

    public Spot(long id, long spotId, boolean available) {
        this.id = id;
        this.spotId = spotId;
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

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "spotId=" + spotId +
                ", available=" + available +
                '}';
    }
}
