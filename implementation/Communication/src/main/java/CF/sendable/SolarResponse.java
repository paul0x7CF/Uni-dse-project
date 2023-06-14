package CF.sendable;

import java.util.Map;
import java.util.UUID;

public class SolarResponse implements ISendable {

    private final UUID responseTimeSlotId;
    private final double solarProduction;

    public SolarResponse(UUID responseTimeSlotId, double solarProduction) {
        this.responseTimeSlotId = responseTimeSlotId;
        this.solarProduction = solarProduction;
    }

    public UUID getResponseTimeSlotId() {
        return responseTimeSlotId;
    }

    public double getSolarProduction() {
        return solarProduction;
    }


}
