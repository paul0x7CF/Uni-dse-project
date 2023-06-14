package MSF.data;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerSolarRequest {
    private int amountOfPanels;
    private double[] area;
    private int[] compassAngle;
    private int[] standingAngle;
    private int[] efficiency;
    private UUID currentTimeSlotID;
    private String senderAddress;
    private int senderPort;
    private UUID senderID;

    public ProsumerSolarRequest(int amountOfPanels, double[] area, int[] compassAngle, int[] standingAngle, int[] efficiency, UUID currentTimeSlotID, String senderAddress, int senderPort, UUID senderID) {
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
