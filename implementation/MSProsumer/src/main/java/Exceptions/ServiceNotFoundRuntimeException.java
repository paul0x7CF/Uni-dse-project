package Exceptions;

public class ServiceNotFoundRuntimeException extends RuntimeException{
    public ServiceNotFoundRuntimeException() {
        super("Service was not found");
    }
}
