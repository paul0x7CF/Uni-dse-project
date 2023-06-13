package loadManager.exchangeManagement;

import java.util.UUID;

public class ExchangeServiceInformation {
    private UUID exchangeId;

    private boolean atCapacity = false;

    public ExchangeServiceInformation(UUID exchangeId) {
        this.exchangeId = exchangeId;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }


    public boolean isAtCapacity() {
        return atCapacity;
    }

    public void setAtCapacity(boolean isAtCapacity) {
        atCapacity = isAtCapacity;
    }

    public Object getExchangeID() {
        return exchangeId;
    }
}
