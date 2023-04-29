package exceptions;

/**
 * Exception thrown when the ProductionForecast is not able to calculate the energy production.
 */
public class ProductionForecastException extends Exception{
    public ProductionForecastException(String message) {
        super(message);
    }
}
