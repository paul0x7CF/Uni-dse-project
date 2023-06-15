package MSP.Data;

import MSP.Configuration.ConfigFileReader;

import java.time.LocalTime;

public class Consumer implements IProsumerDevice {
    private final EConsumerType consumerType;
    private final boolean isHeavyConsumer;
    private final double averageConsumptionpH;
    private final int averageUsageInMinutes;
    private final LocalTime allowedStartConsume;
    private final LocalTime allowedEndConsume;
    private double resultOfForecast;
    private double stillNeededEnergy;

    public Consumer(EConsumerType consumerType) {
        this.consumerType = consumerType;
        this.isHeavyConsumer = Boolean.parseBoolean(ConfigFileReader.getProperty("consumer." + consumerType + ".isHeavyConsumer"));
        this.averageConsumptionpH = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".averageConsumption"));
        this.averageUsageInMinutes = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".usageTime"));
        this.allowedStartConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedStartConsume"));
        this.allowedEndConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedEndConsume"));
        this.stillNeededEnergy = averageUsageInMinutes;
    }

    public double getAverageConsumptionpH() {
        return this.averageConsumptionpH;
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

    public void setResultOfForecast(double resultOfForecast) {
        this.resultOfForecast = resultOfForecast;
    }

    public double getResultOfForecast() {
        return resultOfForecast;
    }
    public void decrementStillNeededEnergy(double energy){
        this.stillNeededEnergy -= energy;
    }

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
