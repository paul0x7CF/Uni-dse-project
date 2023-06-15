package MSP.Exceptions;

public class ServiceNotFoundException extends Exception{
    public ServiceNotFoundException() {
        super("Service was not found");
    }
}
