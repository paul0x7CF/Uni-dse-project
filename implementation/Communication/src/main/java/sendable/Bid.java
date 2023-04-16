package sendable;

import java.util.UUID;

public class Bid implements ISendable {
    private final UUID id = UUID.randomUUID();
    private double volume;
    private double price;

    public Bid(double volume, double price) {
        this.volume = volume;
        this.price = price;
    }

    public UUID getID() {
        return id;
    }

    public double getVolume() {
        return volume;
    }

    public double getPrice() {
        return price;
    }
}
