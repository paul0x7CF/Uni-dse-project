package loadManager.exchangeManagement;

import java.util.UUID;

public class ExchangeServiceInformation {
    private final UUID EXCHANGE_ID;

    private boolean atCapacity = false;

    public ExchangeServiceInformation(UUID exchangeId) {
        this.EXCHANGE_ID = exchangeId;
    }

    public UUID getExchangeId() {
        return EXCHANGE_ID;
    }


    public boolean isAtCapacity() {
        return atCapacity;
    }

    public boolean flipAtCapacity() {
        atCapacity = !atCapacity;
        return atCapacity;
    }

    public Object getExchangeID() {
        return EXCHANGE_ID;
    }
}
