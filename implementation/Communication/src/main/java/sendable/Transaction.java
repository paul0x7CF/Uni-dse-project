package sendable;

import java.util.UUID;

public class Transaction implements ISendable {
    private final UUID id = UUID.randomUUID();
    private final UUID sellerID;
    private final UUID buyerID;
    private final double amount;
    private final double price;

    public Transaction(UUID sellerID, UUID buyerID, double amount, double price) {
        this.sellerID = sellerID;
        this.buyerID = buyerID;
        this.amount = amount;
        this.price = price;
    }

    public UUID getID() {
        return id;
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
}
