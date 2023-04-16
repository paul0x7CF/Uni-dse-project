package sendable;

import java.util.UUID;

public class Sell implements ISendable {
    private final UUID id = UUID.randomUUID();
    private final double volume;
    private final double price;

    public Sell(double volume, double askPrice) {
        this.volume = volume;
        this.price = askPrice;
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
