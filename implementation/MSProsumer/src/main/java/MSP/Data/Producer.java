package MSP.Data;

public record Producer(
        double area,
        int efficiency,
        int compassAngle,
        int standingAngle)
        implements IProsumerDevice {

    @Override
    public <T> T getDevice() {
        return (T) this;
    }
}
