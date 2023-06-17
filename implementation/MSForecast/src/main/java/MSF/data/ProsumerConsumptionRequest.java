package MSF.data;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerConsumptionRequest {
    private final HashMap<String, Double> consumptionMap;
    private final UUID currentTimeSlotID;
    private final String senderAddress;
    private final int senderPort;
    private final UUID senderID;

    public ProsumerConsumptionRequest(EProsumerRequestType type, HashMap<String, Double> consumptionMap, UUID currentTimeSlotID, String senderAddress, int senderPort, UUID senderID) {
        this.consumptionMap = consumptionMap;
        this.currentTimeSlotID = currentTimeSlotID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.senderID = senderID;
    }

    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public UUID getCurrentTimeSlotID() {
        return currentTimeSlotID;
    }

    public UUID getSenderID() {
        return senderID;
    }
}
