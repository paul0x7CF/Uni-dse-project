package loadManager.prosumerActionManagement.priceCalculationStrategy;

import MSP.Exceptions.PriceNotOKException;

public interface PriceMechanism {
    boolean isBidPriceHighEnough(double price) throws PriceNotOKException;

    boolean isAskPriceLowEnough(double price) throws PriceNotOKException;

    double getKWPrice();
}
