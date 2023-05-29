package protocol;


import sendable.ISendable;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID messageID = UUID.randomUUID();
    /**
     * @category: interprets the main and sub category of the message in form of "MainCategory;SubCategory"
     * @mainCategory: Info:
     * @subCategoryInfo: Ping, Register, Unregister, Ack, Error
     * @mainCategory: Auction:
     * @subCategoryAuction: Bid, Sell, BidHigher, SellLower
     * @mainCategory: Exchange:
     * @subCategoryExchange: Transaction, TimeSlot
     * @mainCategory: Forecast:
     * @subCategoryForecast: Production, Consumption
     */
    private String category;
    private UUID senderID;
    private String senderAddress;
    private int senderPort;
    private UUID receiverID;
    private String receiverAddress;
    private int receiverPort;
    private String payload;

    protected Message(String category, UUID senderID, String senderAddress, int senderPort, UUID receiverID, String receiverAddress, int receiverPort, String payload) {
        this.category = category;
        this.senderID = senderID;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverID = receiverID;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.payload = payload;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public String getCategory() {
        return category;
    }

    public UUID getSenderID() {
        return senderID;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public UUID getReceiverID() {
        return receiverID;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public String getPayload() {
        return payload;
    }

    public void setCategory(ECategory mainCat, String subCat) {
        this.category = mainCat.toString() + ";" + subCat;
    }

    public void setSenderID(UUID senderID) {
        this.senderID = senderID;
    }

    public void setSenderAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public void setReceiverID(UUID receiverID) {
        this.receiverID = receiverID;
    }
    public void setReceiverAddress(String temp) {
        this.receiverAddress = temp;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public void setPayload(ISendable payload) {
        this.payload = PayloadConverter.toJSON(payload);
    }

    // converts the payload using the PayloadConverter class and forwards the class which should be instantiated
    public ISendable getSendable(Class<? extends ISendable> type) {
        return PayloadConverter.fromJSON(payload, type);
    }

    public ECategory getMainCategory() {
        //TODO: check if category is valid
        return ECategory.valueOf(category.split(";")[0]);
    }

    public String getSubCategory() {
        //TODO: check if category is valid
        return category.split(";")[1];
    }
}
