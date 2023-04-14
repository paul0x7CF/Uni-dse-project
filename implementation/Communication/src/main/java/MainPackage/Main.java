package MainPackage;

import sendable.ConsumptionRequest;
import sendable.ECategory;
import sendable.SolarResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        double doubleValue = 1.1;

        SolarResponse solarResponse = new SolarResponse(new HashMap<UUID, Double>() {{
            put(UUID.randomUUID(), doubleValue);
            put(UUID.randomUUID(), doubleValue);
            put(UUID.randomUUID(), doubleValue);
        }});





        System.out.println(solarResponse.getSolarProduction().keySet());
    }
}
