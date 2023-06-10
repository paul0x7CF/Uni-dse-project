package mainPackage;

import forecast.ForecastController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import propertyHandler.PropertiesReader;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        final int FORECAST_START_PORT = Integer.parseInt(PropertiesReader.getCommunicationProperty("solarPort"));
        final int FORECAST_AMOUNT = Integer.parseInt(PropertiesReader.getCommunicationProperty("solarAmount"));
        final int PORT_JUMP = Integer.parseInt(PropertiesReader.getCommunicationProperty("portJumpSize"));

        ForecastController forecastController = new ForecastController(FORECAST_START_PORT, FORECAST_AMOUNT, PORT_JUMP);
        forecastController.startForecast();
    }


}
