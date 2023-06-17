package CF.sendable;

/**
 * ErrorInfo is a class that is used to send error messages to another microservice.
 */
public class ErrorInfo implements ISendable {
    private String name;
    private String message;

    public ErrorInfo(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}