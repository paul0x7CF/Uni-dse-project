package MSF.exceptions;

public class UnknownForecastTypeException extends Exception{
    public UnknownForecastTypeException() {
        super("The Forecast Type is unknown.");
    }
}
