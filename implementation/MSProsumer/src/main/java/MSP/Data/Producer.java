package MSP.Data;

import MSP.Configuration.ConfigFileReader;

public class Producer implements IProsumerDevice {

    private final EProducerType producerType;
    private final double area;
    private int efficiency;
    private final int compassAngle;
    private final int standingAngle;

    public Producer(EProducerType producerType) {
        this.producerType = producerType;
        this.area = Double.parseDouble(ConfigFileReader.getProperty("producer." + producerType + ".area"));
        this.efficiency = Integer.parseInt(ConfigFileReader.getProperty("producer." + producerType + ".efficiency"));
        this.compassAngle = Integer.parseInt(ConfigFileReader.getProperty("producer." + producerType + ".compassAngle"));
        this.standingAngle = Integer.parseInt(ConfigFileReader.getProperty("producer." + producerType + ".standingAngle"));
    }

    public EProducerType getProducerType() {
        return producerType;
    }
    public double getArea() {
        return area;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(int efficiency) {
        this.efficiency = efficiency;
    }

    public int getCompassAngle() {
        return compassAngle;
    }

    public int getStandingAngle() {
        return standingAngle;
    }

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
