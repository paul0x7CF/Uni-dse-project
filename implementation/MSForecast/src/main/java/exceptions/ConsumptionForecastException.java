package exceptions;

/**
 * Exception thrown when the ConsumptionForecast is not able to calculate the energy Consumption.
 */
public class ConsumptionForecastException extends Exception{
    public ConsumptionForecastException(String message) {
        super(message);
    }
}
