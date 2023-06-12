package CF.exceptions;

/**
 * This exception is thrown when the message could not be processed by the MessageHandler.
 */
public class MessageProcessingException extends Exception {
    public MessageProcessingException(String message) {
        super(message);
    }
}
