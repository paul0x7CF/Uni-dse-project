package MSF.data;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerConsumptionRequest {
    private HashMap<String, Double> consumptionMap;
    private UUID currentTimeSlotID;
    private String senderAddress;
    private int senderPort;
    private UUID senderID;

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
