package CF.sendable;

import java.util.Map;
import java.util.UUID;

public class SolarResponse implements ISendable {

    private final UUID requestTimeSlotId;
    private Map<UUID, Double> solarProduction;

    public SolarResponse(UUID requestTimeSlotId, Map<UUID, Double> solarProduction) {
        this.requestTimeSlotId = requestTimeSlotId;
        this.solarProduction = solarProduction;
    }

    public UUID getResponseTimeSlotId() {
        return requestTimeSlotId;
    }

    public Map<UUID, Double> getSolarProduction() {
        return solarProduction;
    }

    public void addSolarProduction(UUID id, double production) {
        solarProduction.put(id, production);
    }
}
