package loadManager;

public class Mechanism {
    private double averagePrice;
    private List<double> bidPrices = new ArrayList<double>();
    private List<double> askPrices = new ArrayList<double>();

    public void calculateAveragePrice() {
    }

    public boolean isBidPriceHighEnough(double price) {
        return false;
    }

    public boolean isAskPriceLowEnough(double price) {
        return false;
    }
}