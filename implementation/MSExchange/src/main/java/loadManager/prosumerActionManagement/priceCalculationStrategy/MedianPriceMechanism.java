package loadManager.prosumerActionManagement.priceCalculationStrategy;

import MSP.Exceptions.PriceNotOKException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedianPriceMechanism implements PriceMechanism {
    private List<Double> bidPrices = new ArrayList<>();
    private List<Double> askPrices = new ArrayList<>();

    public boolean isBidPriceHighEnough(double price) throws PriceNotOKException {
        if (price <= 0.0) {
            throw new PriceNotOKException("LOAD_MANAGER: Price can't be zero or lower");
        }

        if (bidPrices.isEmpty()) {
            bidPrices.add(price);
            return true;
        }

        if (price >= getMedianPrice()) {
            bidPrices.add(price);
            return true;
        }
        return false;
    }

    public boolean isAskPriceLowEnough(double price) throws PriceNotOKException {
        if (price <= 0.0) {
            throw new PriceNotOKException("LOAD_MANAGER: Price can't be zero or lower");
        }

        if (askPrices.isEmpty()) {
            askPrices.add(price);
            return true;
        }

        if (price <= getMedianPrice()) {
            askPrices.add(price);
            return true;
        }
        return false;
    }

    public double getKWPrice() {
        return getMedianPrice();
    }

    private double getMedianPrice() {
        List<Double> allPrices = new ArrayList<>(bidPrices);
        allPrices.addAll(askPrices);
        Collections.sort(allPrices);

        int size = allPrices.size();
        if (size % 2 == 0) {
            double middle1 = allPrices.get(size / 2 - 1);
            double middle2 = allPrices.get(size / 2);
            return (middle1 + middle2) / 2.0;
        } else {
            return allPrices.get(size / 2);
        }
    }
}
