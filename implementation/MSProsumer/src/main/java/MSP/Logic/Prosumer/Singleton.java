package MSP.Logic.Prosumer;

public class Singleton {
    private static Singleton instance = null;
    private ConsumptionBuilding consumptionBuilding;
    private Singleton() {
        // Exists only to defeat instantiation.
    }
    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public ConsumptionBuilding getConsumptionBuilding() {
        return consumptionBuilding;
    }

    public void setConsumptionBuilding(ConsumptionBuilding consumptionBuilding) {
        this.consumptionBuilding = consumptionBuilding;
        System.out.println("--------------------------ConsumptionBuilding set" + this.consumptionBuilding);
    }
}
