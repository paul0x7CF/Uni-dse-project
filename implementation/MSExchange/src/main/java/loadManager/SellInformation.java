package loadManager;

import sendable.Sell;

import java.util.UUID;

public class SellInformation {
    private Sell sell;
    private UUID exchangeID;

    public SellInformation(Sell sell, UUID exchangeID) {
        this.sell = sell;
        this.exchangeID = exchangeID;
    }

    public Sell getSell() {
        return sell;
    }

    public UUID getExchangeID() {
        return exchangeID;
    }
}
