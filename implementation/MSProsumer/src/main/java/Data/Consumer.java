package Data;

public class Consumer implements IProsumerDevice{

    private double baseLoad;
    private EConsumerType consumerType;
    private boolean isHeavyConsumer;
    private double averageConsumption;

    public Consumer(EConsumerType consumerType) {}

    public double getAverageConsumption() {
        return this.averageConsumption;
    }

    public void isHeavyConsumer() {}

    public EConsumerType getConsumerType() {
        return this.consumerType;
    }

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
