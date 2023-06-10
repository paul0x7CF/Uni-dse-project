package MSF.exceptions;

public class MessageNotSupportedException extends Exception{
    public MessageNotSupportedException() {
        super("The Message is not supported by the Handler");
    }
}
