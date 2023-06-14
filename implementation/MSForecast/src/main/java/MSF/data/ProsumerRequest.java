package MSF.data;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerRequest {
    private EProsumerRequestType type;
    private int amountOfPanels;
    private double[] area;
    private int[] compassAngle;
    private int[] standingAngle;
    private int[] efficiency;
    private HashMap<String, Double> consumptionMap;
    private UUID currentTimeSlotID;
    private String senderAddress;
    private int senderPort;
    private UUID senderID;

    public ProsumerRequest(EProsumerRequestType type, int amountOfPanels, double[] area, int[] compassAngle, int[] standingAngle, int[] efficiency, UUID currentTimeSlotID, String senderAddress, int senderPort, UUID senderID) {
        this.type = type;
        this.amountOfPanels = amountOfPanels;
        this.area = area;
        this.compassAngle = compassAngle;
        this.standingAngle = standingAngle;
        this.efficiency = efficiency;
        this.currentTimeSlotID = currentTimeSlotID;
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

    public int getAmountOfPanels() {
        return amountOfPanels;
    }

    public double[] getArea() {
        return area;
    }

    public int[] getCompassAngle() {
        return compassAngle;
    }

    public int[] getStandingAngle() {
        return standingAngle;
    }

    public int[] getEfficiency() {
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
