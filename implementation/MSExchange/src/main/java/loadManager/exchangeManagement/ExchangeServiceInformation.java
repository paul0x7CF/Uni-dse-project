package loadManager.exchangeManagement;

import java.util.UUID;

public class ExchangeServiceInformation {
    private UUID exchangeId;
    private String ipAddress;
    private int port;

    private boolean atCapacity = false;

    public ExchangeServiceInformation(UUID exchangeId, String ipAddress, int port) {
        this.exchangeId = exchangeId;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
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
