package loadManager;

import java.util.UUID;

public class SellInformation {
    private UUID prosumerID;
    private double price;
    private double kwh;
    private UUID exchangeID;

    public SellInformation(UUID prosumerID, double price, double kwh, UUID exchangeID) {
        this.prosumerID = prosumerID;
        this.price = price;
        this.kwh = kwh;
        this.exchangeID = exchangeID;
    }
}
