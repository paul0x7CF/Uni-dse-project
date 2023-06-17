package CF.sendable;

import java.util.UUID;

/**
 * This class is used to send a transaction from the exchange to the prosumer and storage. It contains the sellerID,
 * buyerID, amount, price and auctionID. The auctionID is not used by the prosumer.
 */
public class Transaction implements ISendable {

    private final UUID transactionID = UUID.randomUUID();
    private final UUID sellerID;
    private final UUID buyerID;
    private final double amount;
    private final double price;
    private final UUID auctionID;

    public Transaction(UUID sellerID, UUID buyerID, double amount, double price, UUID auctionID) {
        this.sellerID = sellerID;
        this.buyerID = buyerID;
        this.amount = amount;
        this.price = price;
        this.auctionID = auctionID;
    }

    public UUID getTransactionID() {
        return transactionID;
    }

    public UUID getSellerID() {
        return sellerID;
    }

    public UUID getBuyerID() {
        return buyerID;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public UUID getAuctionID() {
        return auctionID;
    }
}
