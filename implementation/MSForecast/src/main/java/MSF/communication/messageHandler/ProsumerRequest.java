package MSF.communication.messageHandler;

import CF.sendable.TimeSlot;

import java.util.HashMap;
import java.util.UUID;

public class ProsumerRequest {
    private EProsumerRequestType type;
    private int area;
    private int angle;
    private int efficiency;
    private HashMap<String, Double> consumptionMap;
    private UUID currentTimeSlotID;

    public ProsumerRequest(EProsumerRequestType type, int area, int angle, int efficiency) {
        this.type = type;
        this.area = area;
        this.angle = angle;
        this.efficiency = efficiency;
    }

    public ProsumerRequest(EProsumerRequestType type, HashMap<String, Double> consumptionMap, UUID currentTimeSlotID) {
        this.type = type;
        this.consumptionMap = consumptionMap;
        this.currentTimeSlotID = currentTimeSlotID;
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
}
