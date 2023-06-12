package MSF.communication.messageHandler;

import java.util.HashMap;

public class ProsumerRequest {
    private EProsumerRequestType type;
    private int area;
    private int angle;
    private int efficiency;
    private HashMap<String, Double> consumptionMap;

    public ProsumerRequest(EProsumerRequestType type, int area, int angle, int efficiency) {
        this.type = type;
        this.area = area;
        this.angle = angle;
        this.efficiency = efficiency;
    }

    public ProsumerRequest(EProsumerRequestType type, HashMap<String, Double> consumptionMap) {
        this.type = type;
        this.consumptionMap = consumptionMap;
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
}
