package MSP.Exceptions;

import CF.sendable.Sell;

public class InvalidSellException extends Exception {
    private Sell sell;

    public InvalidSellException(String message, Sell sell) {
        super(message);
        this.sell = sell;
    }

    public Sell getSell() {
        return this.sell;
    }
}
