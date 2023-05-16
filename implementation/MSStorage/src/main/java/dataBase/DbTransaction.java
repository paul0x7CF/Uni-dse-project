package dataBase;


import sendable.Transaction;

import javax.persistence.*;
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

    public DbTransaction() {
    }
    public DbTransaction(UUID sellerID, UUID buyerID, double amount, double price) {
        this.transactionID = UUID.randomUUID();
        this.sellerId = sellerID;
        this.buyerId = buyerID;
        this.amount = amount;
        this.price = price;

    }


    public UUID getTransactionID() {
        return transactionID;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

}
