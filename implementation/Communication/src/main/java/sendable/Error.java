package sendable;

public class Error implements ISendable {
    private ECategory category;
    private String message;

    public Error(ECategory category, String message) {
        this.category = category;
        this.message = message;
    }

    public ECategory getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }
}
