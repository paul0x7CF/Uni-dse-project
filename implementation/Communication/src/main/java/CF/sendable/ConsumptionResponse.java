package CF.sendable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConsumptionResponse implements ISendable {
    private final HashMap<String, Double> consumptionMap;
    private final UUID requestTimeSlotId;

    public ConsumptionResponse(HashMap<String, Double> consumptionMap, UUID requestTimeSlotId) {
        this.consumptionMap = consumptionMap;
        this.requestTimeSlotId = requestTimeSlotId;
    }

    //TODO: Change UUID to String
    public HashMap<String, Double> getConsumptionMap() {
        return consumptionMap;
    }

    public UUID getRequestTimeSlotId() {
        return requestTimeSlotId;
    }
}
