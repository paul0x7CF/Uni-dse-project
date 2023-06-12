package CF.exceptions;

/**
 * Exception thrown when the ack timeout is reached.
 */
public class AckTimeoutException extends Exception {
    public AckTimeoutException(String message) {
        super(message);
    }
}
