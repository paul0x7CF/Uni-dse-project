package MSP.Data;

import CF.sendable.TimeSlot;
import MSP.Clock.TimeProvider;
import MSP.Configuration.ConfigFileReader;

import java.time.LocalTime;

public class Consumer implements IProsumerDevice {

    private final TimeProvider clock = new TimeProvider();
    private final EConsumerType consumerType;
    private final boolean isHeavyConsumer;
    private final double averageConsumption;
    private final int averageUsageInMinutes;
    private final LocalTime allowedStartConsume;
    private final LocalTime allowedEndConsume;

    public Consumer(EConsumerType consumerType) {
        this.consumerType = consumerType;
        this.isHeavyConsumer = Boolean.parseBoolean(ConfigFileReader.getProperty("consumer." + consumerType + ".isHeavyConsumer"));
        this.averageConsumption = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".averageConsumption"));
        this.averageUsageInMinutes = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".usageTime"));
        this.allowedStartConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedStartConsume"));
        this.allowedEndConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedEndConsume"));
    }

    public double getAverageConsumption() {
        return this.averageConsumption;
    }

    public boolean isHeavyConsumer() {
        return this.isHeavyConsumer;
    }

    public EConsumerType getConsumerType() {
        return this.consumerType;
    }

    public boolean isAllowedToConsume(LocalTime timeSlotStart) {
        return timeSlotStart.isAfter(allowedStartConsume) && timeSlotStart.isBefore(allowedEndConsume);
    }

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
