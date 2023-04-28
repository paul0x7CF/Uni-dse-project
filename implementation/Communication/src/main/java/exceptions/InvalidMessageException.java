package exceptions;

/**
 * This exception is thrown by the MessageBuilder when the message is invalid.
 */
public class InvalidMessageException extends Exception {
    public InvalidMessageException(String message) {
        super(message);
    }
}
