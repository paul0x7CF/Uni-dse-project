package Data;

public class Producer implements IProsumerDevice{

    private final double area;

    private final int efficiency;

    private final int compassAngle;

    private final int standingAngle;

    public Producer(final double area, final int efficiency, final int compassAngle, final int standingAngle) {
        this.area = area;
        this.efficiency = efficiency;
        this.compassAngle = compassAngle;
        this.standingAngle = standingAngle;
    }

    public double getArea() {
        return area;
    }

    public int getEfficiency() {
        return efficiency;
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
