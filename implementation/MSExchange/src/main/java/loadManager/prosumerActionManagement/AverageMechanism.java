package loadManager.prosumerActionManagement;

import loadManager.Exceptions.PriceNotOKException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AverageMechanism {
    private final int K_VALUES = 100;
    private double averagePrice = 0.0;
    private List<Double> bidPrices = new ArrayList<Double>();
    private List<Double> askPrices = new ArrayList<Double>();

    public boolean isBidPriceHighEnough(double price) throws PriceNotOKException {
        if (price <= 0.0) {
            throw new PriceNotOKException("Price can't be zero or lower");
        }

        if (averagePrice == 0.0) {
            updateList(price, EAction.Bid);
            return true;
        }

        if (price >= averagePrice) {
            updateList(price, EAction.Bid);
            return true;
        }
        return false;
    }

    public boolean isAskPriceLowEnough(double price) throws PriceNotOKException {
        if (price <= 0.0) {
            throw new PriceNotOKException("Price can't be zero or lower");
        }

        if (averagePrice == 0.0) {
            updateList(price, EAction.Sell);
            return true;
        }

        if (price <= averagePrice) {
            updateList(price, EAction.Sell);
            return true;
        }
        return false;
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

        List<Double> sortedBidPrices = new ArrayList<>(bidPrices);
        List<Double> sortedAskPrices = new ArrayList<>(askPrices);
        Collections.sort(sortedBidPrices);
        Collections.sort(sortedAskPrices);

        double bidPriceK = sortedBidPrices.get(k - 1);
        double askPriceK = sortedAskPrices.get(k - 1);
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