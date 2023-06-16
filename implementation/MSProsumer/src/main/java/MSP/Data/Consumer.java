package MSP.Data;

import MSP.Configuration.ConfigFileReader;

import java.time.LocalTime;

public class Consumer implements IProsumerDevice {
    private final EConsumerType consumerType;
    private final boolean isHeavyConsumer;
    private double averageConsumption;
    private final LocalTime allowedStartConsume;
    private final LocalTime allowedEndConsume;
    private double resultOfForecast;
    private double stillNeededEnergy;

    public Consumer(EConsumerType consumerType) {
        this.consumerType = consumerType;
        this.isHeavyConsumer = Boolean.parseBoolean(ConfigFileReader.getProperty("consumer." + consumerType + ".isHeavyConsumer"));
        this.averageConsumption = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".averageConsumption"));
        this.allowedStartConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedStartConsume"));
        this.allowedEndConsume = LocalTime.parse(ConfigFileReader.getProperty("consumer." + consumerType + ".allowedEndConsume"));
        this.stillNeededEnergy = Integer.parseInt(ConfigFileReader.getProperty("consumer." + consumerType + ".usageTime"));
    }

    public double getAverageConsumption() {
        return this.averageConsumption;
    }

    public boolean setAverageConsumption(double averageConsumption) {
        if (averageConsumption < 0) {
            return false;
        }
        this.averageConsumption = averageConsumption;
        return true;
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
