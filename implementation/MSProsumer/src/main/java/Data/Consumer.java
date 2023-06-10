package Data;

import Configuration.ConfigFileReader;

public class Consumer implements IProsumerDevice {

    private double baseLoad;
    private EConsumerType consumerType;
    private boolean isHeavyConsumer;
    private double averageConsumption;

    public Consumer(EConsumerType consumerType) {
        this.consumerType = consumerType;
        this.isHeavyConsumer = Boolean.parseBoolean(ConfigFileReader.getProperty("consumer." + consumerType + ".isHeavyConsumer"));
        this.baseLoad = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".baseLoad"));
        this.averageConsumption = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".averageConsumption"));

    }

    public double getAverageConsumption() {
        return this.averageConsumption;
    }

    public void isHeavyConsumer() {
    }

    public EConsumerType getConsumerType() {
        return this.consumerType;
    }

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
