package sendable;

public class ConsumptionRequest implements ISendable {
    private final String type;
    private final double consumption;

    public ConsumptionRequest(String type, double consumption) {
        this.type = type;
        this.consumption = consumption;
    }

    public String getType() {
        return type;
    }

    public double getConsumption() {
        return consumption;
    }
}