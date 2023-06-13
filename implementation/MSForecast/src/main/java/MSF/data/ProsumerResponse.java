package MSF.data;

import java.util.HashMap;

public class ProsumerResponse {
    private EProsumerRequestType type;
    private HashMap<String, Double> consumptionMap;

    //TODO: SolarResponse

    public ProsumerResponse(HashMap<String, Double> consumptionMap) {
        this.consumptionMap = consumptionMap;
    }

    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }

    public EProsumerRequestType getType() {
        return type;
    }
}
