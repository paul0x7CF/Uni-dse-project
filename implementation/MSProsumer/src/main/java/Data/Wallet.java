package Data;

public class Wallet {

    private double cashBalance;
    private double minAskPrice;
    private double maxBuyPrice;

    public Wallet(final double cashBalance, final double minAskPrice, final double maxBuyPrice) {
        this.cashBalance = cashBalance;
        this.minAskPrice = minAskPrice;
        this.maxBuyPrice = maxBuyPrice;
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
