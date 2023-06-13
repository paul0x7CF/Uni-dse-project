package MSP.Exceptions;

public class UnknownForecastResponseException extends Exception{
    public UnknownForecastResponseException(String message) {
        super(message);
    }

    public UnknownForecastResponseException() {
        super("Couldn't find an expected response for the given TimeSlotId");
    }
}
