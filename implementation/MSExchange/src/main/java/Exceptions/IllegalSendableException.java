package Exceptions;

public class IllegalSendableException extends IllegalArgumentException {
    public IllegalSendableException(String message) {
        super(message);
    }
}
