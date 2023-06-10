package CF.sendable;

public class SolarRequest implements ISendable {
    private final int area;
    private final int angle;
    private final int efficiency;

    public SolarRequest(int area, int angle, int efficiency) {
        this.area = area;
        this.angle = angle;
        this.efficiency = efficiency;
    }

    public int getArea() {
        return area;
    }

    public int getAngle() {
        return angle;
    }

    public int getEfficiency() {
        return efficiency;
    }
}
