package CF.exceptions;

/**
 * This exception is thrown when an error occurs during marshalling.
 */
public class MarshallingException extends Exception {
    public MarshallingException(String message) {
        super(message);
    }
}
