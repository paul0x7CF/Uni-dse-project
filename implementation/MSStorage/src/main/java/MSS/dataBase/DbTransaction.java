package MSS.dataBase;


import CF.sendable.Transaction;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class DbTransaction {

    @Id
    private UUID transactionID;

    @Column(name = "seller_id")
    private UUID sellerId;

    @Column(name = "buyer_id")
    private UUID buyerId;

    @Column(name = "amount")
    private double amount;

    @Column(name = "price")
    private double price;

    @Column(name = "auction_id")
    private UUID auctionID;

    @Column(name = "timestamp")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime timestamp;

    public DbTransaction() {
    }

    public DbTransaction(UUID sellerID, UUID buyerID, double amount, double price, UUID auctionID) {
        this.transactionID = UUID.randomUUID();
        this.sellerId = sellerID;
        this.buyerId = buyerID;
        this.amount = amount;
        this.price = price;
        this.auctionID = auctionID;
        this.timestamp = LocalDateTime.now();

    }

    public UUID getTransactionID() {
        return transactionID;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public UUID getBuyerId() {
        return buyerId;
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
