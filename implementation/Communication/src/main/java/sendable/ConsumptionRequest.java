package sendable;

import java.util.HashMap;

public class ConsumptionRequest implements ISendable {

    private final HashMap<String, Double> consumptionMap;

    public ConsumptionRequest(HashMap<String, Double> consumptionMap) {
        this.consumptionMap = consumptionMap;

    }

    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }
}