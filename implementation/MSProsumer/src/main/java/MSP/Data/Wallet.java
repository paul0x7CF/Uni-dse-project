package MSP.Data;

public class Wallet {

    private double cashBalance;
    private double minAskPrice;
    private double maxBuyPrice;

    public Wallet(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getCashBalance() {
        return this.cashBalance;
    }

    public double getMinAskPrice() {
        return this.minAskPrice;
    }

    public double getMaxBuyPrice() {
        return this.maxBuyPrice;
    }

    public void incrementCashBalance(double cashBalance) {
        this.cashBalance += cashBalance;
    }
    public void decrementCashBalance(double cashBalance) {
        this.cashBalance -= cashBalance;
    }
}
