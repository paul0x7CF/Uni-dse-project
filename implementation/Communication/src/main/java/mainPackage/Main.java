package mainPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.SolarResponse;

import java.util.HashMap;
import java.util.UUID;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        double doubleValue = 1.1;
        logger.info(doubleValue);

        SolarResponse solarResponse = new SolarResponse(new HashMap<UUID, Double>() {{
            put(UUID.randomUUID(), doubleValue);
            put(UUID.randomUUID(), doubleValue);
            put(UUID.randomUUID(), doubleValue);
        }});


        System.out.println(solarResponse.getSolarProduction().keySet());
    }
}
