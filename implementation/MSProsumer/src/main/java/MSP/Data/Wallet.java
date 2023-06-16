package MSP.Data;

import MSP.Exceptions.WalletEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Wallet {

    private static final Logger logger = LogManager.getLogger(Wallet.class);

    private double cashBalance;
    private double sellPrice;
    private double bidPrice;

    public Wallet(double cashBalance) {
        this.cashBalance = cashBalance;
        this.sellPrice = 1.4;
        this.bidPrice = 1.2;
        // Todo @Paul set Wallet Price
    }

    public double getCashBalance() {
        return this.cashBalance;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public double getLowerSellPrice(double minPrice) {
        return minPrice * 0.85;
    }

    public double getHigherBidPrice(double maxPrice) {
        return maxPrice * 1.15;
    }

    public double getBidPrice() {
        return this.bidPrice;
    }

    public void incrementCashBalance(double moneyToAdd) {
        this.cashBalance += moneyToAdd;
    }

    public void decrementCashBalance(double moneyToRemove) {
        if(this.cashBalance - moneyToRemove < 0) {
            try {
                throw new WalletEmptyException("only {} in wallet but {} needed to be removed; Prosumer is bankrupt");
            } catch (WalletEmptyException e) {
                logger.warn(e.getMessage());
            }
        }else {
            this.cashBalance += moneyToRemove;
        }
    }
}
