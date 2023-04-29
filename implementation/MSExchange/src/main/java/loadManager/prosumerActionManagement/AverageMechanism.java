package loadManager.prosumerActionManagement;

import java.util.ArrayList;
import java.util.List;

public class AverageMechanism {
    private final int K_VALUES = 100;
    private double averagePrice;
    private List<Double> bidPrices = new ArrayList<Double>();
    private List<Double> askPrices = new ArrayList<Double>();


    public boolean isBidPriceHighEnough(double price) {
        return false;
    }

    public boolean isAskPriceLowEnough(double price) {
        return false;
    }

    public double getAveragePrice() {
        return 0;
    }

    private void calculateAveragePrice() {
    }

    private void updateList() {
    }


}