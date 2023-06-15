package MSF.mainPackage;

public class TEST {
    public static void main(String[] args) {
        double frequency = 1; // Adjust this value for the desired frequency
        double amplitude = 100; // Adjust this value for the desired amplitude
        double phase = 0.0;

        double energyConsumption = amplitude * Math.sin((2 * Math.PI * frequency * 3600 + phase) * (Math.PI / 180));

        double consumption = (double) 100 / ((double) 3600 / 60);
        System.out.println("energyConsumption: " + energyConsumption / 60);

        System.out.println("Consumption: " + consumption);
    }
}
