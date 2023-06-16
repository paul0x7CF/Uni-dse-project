package loadManager;

import CF.sendable.Sell;

import java.util.UUID;

public class SellInformation {
    private Sell sell;
    private UUID exchangeID;

    public SellInformation(Sell sell, UUID exchangeID) {
        this.sell = sell;
        this.exchangeID = exchangeID;
    }

    public SellInformation(UUID exchangeID) {
        this.exchangeID = exchangeID;
    }

    public Sell getSell() {
        return sell;
    }

    public void setSell(Sell sell) {
        this.sell = sell;
    }

    public UUID getExchangeID() {
        return exchangeID;
    }
}
