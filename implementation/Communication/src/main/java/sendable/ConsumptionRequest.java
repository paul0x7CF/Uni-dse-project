package sendable;

import java.util.HashMap;
import java.util.UUID;

public class ConsumptionRequest implements ISendable {

    private final HashMap<String, Double> consumptionMap;
    private final UUID requestTimeSlotId;

    public ConsumptionRequest(HashMap<String, Double> consumptionMap, UUID requestTimeSlotId) {
        this.consumptionMap = consumptionMap;
        this.requestTimeSlotId = requestTimeSlotId;

    }

    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }

    public UUID getRequestTimeSlotId() {
        return requestTimeSlotId;
    }
}