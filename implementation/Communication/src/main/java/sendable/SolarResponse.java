package sendable;

import java.util.Map;
import java.util.UUID;

public class SolarResponse implements ISendable {
    private Map<UUID, Double> solarProduction;

    public SolarResponse(Map<UUID, Double> solarProduction) {
        this.solarProduction = solarProduction;
    }

    public Map<UUID, Double> getSolarProduction() {
        return solarProduction;
    }

}
