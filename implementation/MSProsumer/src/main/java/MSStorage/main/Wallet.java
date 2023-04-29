package MSStorage.main;

public class Wallet {
    private double cashBalance;

    public void increaseCashBalance(double value) {
        cashBalance += value;
    }

    public void decrementCashBalance(double value) {
        cashBalance -= value;
    }
}
