package CF.sendable;

import java.util.UUID;

public class SolarRequest implements ISendable {

    private final UUID requestTimeSlotId;
    private final int amountOfPanels;
    private final double[] area;
    private final int[] efficiency;
    private final int[] compassAngle;
    private final int[] standingAngle;

    public SolarRequest(UUID requestTimeSlotId, int amountOfPanels,
                        double[] area, int[] efficiency,
                        int[] compassAngle, int[] standingAngle) {
        this.requestTimeSlotId = requestTimeSlotId;
        this.amountOfPanels = amountOfPanels;
        this.area = area;
        this.efficiency = efficiency;
        this.compassAngle = compassAngle;
        this.standingAngle = standingAngle;

        if(area.length != amountOfPanels || efficiency.length != amountOfPanels ||
                compassAngle.length != amountOfPanels || standingAngle.length != amountOfPanels) {
            throw new IllegalArgumentException("The length of the arrays must be equal to the amount of panels");
        }

    }

    public UUID getRequestTimeSlotId() {
        return requestTimeSlotId;
    }

    public int getAmountOfPanels() {
        return amountOfPanels;
    }

    public double[] getArea() {
        return area;
    }

    public int[] getEfficiency() {
        return efficiency;
    }

    public int[] getCompassAngle() {
        return compassAngle;
    }

    public int[] getStandingAngle() {
        return standingAngle;
    }
}
