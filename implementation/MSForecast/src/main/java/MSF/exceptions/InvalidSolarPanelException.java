package MSF.exceptions;

/**
 * Exception thrown when sent SolarRequest is not valid.
 */
public class InvalidSolarPanelException extends Exception{
    public InvalidSolarPanelException(String message) {
        super(message);
    }
}
