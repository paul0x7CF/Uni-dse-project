package MSF.exceptions;

public class UnknownMessageException extends Exception{
    public UnknownMessageException() {
        super("Message was Ignored because it was unknown");
    }
}
