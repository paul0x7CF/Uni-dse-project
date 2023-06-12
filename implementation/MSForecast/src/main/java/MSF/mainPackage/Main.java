package MSF.mainPackage;

import MSF.forecast.MSForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import MSF.propertyHandler.PropertiesReader;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        final int FORECAST_START_PORT = Integer.parseInt(PropertiesReader.getCommunicationProperty("solarPort"));
        final int FORECAST_AMOUNT = Integer.parseInt(PropertiesReader.getCommunicationProperty("solarAmount"));
        final int PORT_JUMP = Integer.parseInt(PropertiesReader.getCommunicationProperty("portJumpSize"));

        for (int i = 0; i < FORECAST_AMOUNT; i++) {
            new Thread(new MSForecast(FORECAST_START_PORT + (i * PORT_JUMP)), "Forecast-" + i).start();
        }

        logger.info("{} Forecasts created", FORECAST_AMOUNT);
    }
}
