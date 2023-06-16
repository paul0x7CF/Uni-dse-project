package MSF.mainPackage;

import MSF.data.EForecastType;
import MSF.forecast.MSForecast;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import MSF.propertyHandler.PropertiesReader;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        final int FORECAST_START_PORT = Integer.parseInt(PropertiesReader.getCommunicationProperty("forecastPort"));
        final int FORECAST_AMOUNT = Integer.parseInt(PropertiesReader.getCommunicationProperty("forecastAmount"));
        final int PORT_JUMP = Integer.parseInt(PropertiesReader.getCommunicationProperty("portJumpSize"));

        new Thread(new MSForecast(FORECAST_START_PORT + (0 * PORT_JUMP), EForecastType.APOLIS), "Forecast-APOLIS").start();
        new Thread(new MSForecast(FORECAST_START_PORT + (1 * PORT_JUMP), EForecastType.GROUNDSTATION), "Forecast-GROUNDSTATION").start();
        new Thread(new MSForecast(FORECAST_START_PORT + (2 * PORT_JUMP), EForecastType.INCA_L), "Forecast-INCA_L").start();

        logger.info("{} Forecasts created", FORECAST_AMOUNT);
        logger.debug("DEEEEEEEEEEEEEB");
    }
}
