package exceptions;

/**
 * This exception is thrown by the MessageFactory when the message is invalid.
 */
public class InvalidMessageException extends Exception {
    public InvalidMessageException(String message) {
        super(message);
    }
}
