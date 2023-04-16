package loadManager;

import java.util.UUID;

public class ExchangeServiceInformation {
    private UUID exchangeId;
    private int ipAddress;
    private int port;

    private boolean atCapacity=false;

    public ExchangeServiceInformation(UUID exchangeId, int ipAddress, int port) {
        this.exchangeId = exchangeId;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public UUID getExchangeId() {
        return exchangeId;
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public boolean isAtCapacity(){
        return atCapacity;
    }

    public void setAtCapacity(boolean isAtCapacity){
        atCapacity=isAtCapacity;
    }
}
