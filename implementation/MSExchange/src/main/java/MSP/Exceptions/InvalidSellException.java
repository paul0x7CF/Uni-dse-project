package MSP.Exceptions;

import java.util.UUID;

public class InvalidSellException extends Exception {
    private UUID sellerID;

    public InvalidSellException(String message, UUID sellerID) {
        super(message);
        this.sellerID = sellerID;
    }

    public UUID getSellerID() {
        return sellerID;
    }
}
