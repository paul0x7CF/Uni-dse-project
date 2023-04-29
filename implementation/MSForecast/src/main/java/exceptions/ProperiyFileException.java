package exceptions;

/**
 * Exception thrown when the Property file cannot be read.
 */
public class ProperiyFileException extends Exception{
    public ProperiyFileException(String message) {
        super(message);
    }
}
