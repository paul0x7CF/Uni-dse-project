package CF.sendable;

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