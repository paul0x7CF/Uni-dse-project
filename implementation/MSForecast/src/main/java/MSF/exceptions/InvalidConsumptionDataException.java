package MSF.exceptions;

/**
 * Exception thrown when the sent Consumer is not valid.
 */
public class InvalidConsumptionDataException extends Exception{
    public InvalidConsumptionDataException(String message) {
        super(message);
    }
}
