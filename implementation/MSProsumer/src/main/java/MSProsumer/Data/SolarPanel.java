package MSProsumer.Data;

public class SolarPanel {

    private double area;

    private int efficiency;

    private int compassAngle;

    private int standingAngle;

    public SolarPanel(final double area, final int efficiency, final int compassAngle, final int standingAngle) {
        this.area = area;
        this.efficiency = efficiency;
        this.compassAngle = compassAngle;
        this.standingAngle = standingAngle;
    }
}
