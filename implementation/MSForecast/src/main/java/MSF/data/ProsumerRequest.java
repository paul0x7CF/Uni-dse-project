package MSF.data;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerRequest {
    private EProsumerRequestType type;
    private int area;
    private int angle;
    private int efficiency;
    private HashMap<String, Double> consumptionMap;
    private UUID currentTimeSlotID;
    private String senderAddress;
    private int senderPort;
    private UUID senderID;

    public ProsumerRequest(EProsumerRequestType type, int area, int angle, int efficiency, String senderAddress, int senderPort, UUID senderID) {
        this.type = type;
        this.area = area;
        this.angle = angle;
        this.efficiency = efficiency;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.senderID = senderID;
    }

    public ProsumerRequest(EProsumerRequestType type, HashMap<String, Double> consumptionMap, UUID currentTimeSlotID, String senderAddress, int senderPort, UUID senderID) {
        this.type = type;
        this.consumptionMap = consumptionMap;
        this.currentTimeSlotID = currentTimeSlotID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.senderID = senderID;
    }

    public int getArea() {
        return area;
    }

    public int getAngle() {
        return angle;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }

    public EProsumerRequestType getType() {
        return type;
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
