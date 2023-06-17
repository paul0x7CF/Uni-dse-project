package MSS.data;

import MSS.exceptions.WalletEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Wallet {

    private static final Logger logger = LogManager.getLogger(Wallet.class);

    private double cashBalance;

    public Wallet(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCashBalance() {
        return this.cashBalance;
    }

    public void incrementCashBalance(double moneyToAdd) {
        this.cashBalance += moneyToAdd;
        logger.info("Wallet cashBalance increased by {}; new cashBalance: {}", moneyToAdd, this.cashBalance);
    }

    public void decrementCashBalance(double moneyToRemove) {
        if(this.cashBalance - moneyToRemove < 0) {
            try {
                throw new WalletEmptyException("only {} in wallet but {} needed to be removed");
            } catch (WalletEmptyException e) {
                logger.debug(e.getMessage()+"; adding money to wallet because Storage has endless money");
                final double RESET_MONEY = 999.99;
                this.cashBalance = RESET_MONEY;
                decrementCashBalance(moneyToRemove);
            }
        }else {
            this.cashBalance -= moneyToRemove;
            logger.info("Wallet cashBalance decreased by {}; new cashBalance: {}", moneyToRemove, this.cashBalance);
        }
    }
}
