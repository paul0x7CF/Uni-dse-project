package loadManager.prosumerActionManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AverageMechanism {
    private final int K_VALUES = 100;
    private double averagePrice;
    private List<Double> bidPrices = new ArrayList<Double>();
    private List<Double> askPrices = new ArrayList<Double>();

    public boolean isBidPriceHighEnough(double price) {
        return price >= averagePrice;
    }

    public boolean isAskPriceLowEnough(double price) {
        return price <= averagePrice;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    private void calculateAveragePrice() {
        int k = Math.min(bidPrices.size(), askPrices.size());
        if (k < K_VALUES) {
            averagePrice = 0.0;
            return;
        }
        Collections.sort(bidPrices);
        Collections.sort(askPrices);

        double bidPriceK = bidPrices.get(k - 1);
        double askPriceK = askPrices.get(k - 1);
        averagePrice = (bidPriceK + askPriceK) / 2.0;
    }


    private void updateList(double price, EAction action) {
        switch (action) {
            case Bid -> bidPrices.add(price);
            case Sell -> askPrices.add(price);
        }

        if (bidPrices.size() > K_VALUES) {
            bidPrices.remove(0);
        }
        if (askPrices.size() > K_VALUES) {
            askPrices.remove(0);
        }

        calculateAveragePrice();
    }


}