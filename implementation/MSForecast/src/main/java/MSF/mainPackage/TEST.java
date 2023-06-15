package MSF.mainPackage;

public class TEST {
    public static void main(String[] args) {
        double irradiation = (double) (6 * 1000) / 24;
        double production = irradiation * 2.5 * 0.20 * Math.cos(140 * (Math.PI / 180)) * Math.cos(20 * (Math.PI / 180));

        double angleRad = 220 * (Math.PI / 180);
        System.out.println("Cosinus: " + Math.cos(angleRad));
        System.out.println("Production: " + production);
    }
}
