package sendable;

import java.util.Map;
import java.util.UUID;

public class ConsumptionResponse implements ISendable {
    private Map<UUID, Double> consumption;

    public ConsumptionResponse(Map<UUID, Double> consumption) {
        this.consumption = consumption;
    }

    public Map<UUID, Double> getConsumption() {
        return consumption;
    }
}
