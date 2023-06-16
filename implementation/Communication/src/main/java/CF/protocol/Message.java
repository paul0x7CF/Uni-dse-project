package CF.protocol;


import CF.exceptions.InvalidMessageException;
import CF.messageHandling.InfoMessageHandler;
import CF.sendable.ISendable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.UUID;

/**
 * Message class that represents a message that is sent between components. It is used to send messages between
 * components. The message has a category, a sender and a receiver. The payload is optional and should not be null.
 * <p>
 * The Message class has a protected constructor so it can only be created by the {@link MessageFactory}.
 */
public class Message implements Serializable {
    private static final Logger log = LogManager.getLogger(Message.class);

    private final UUID messageID = UUID.randomUUID();
    private final String senderAddress;
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

    public void setSenderID(UUID senderID) {
        this.senderID = senderID;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public UUID getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(UUID receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String temp) {
        this.receiverAddress = temp;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(ISendable payload) {
        this.payload = PayloadConverter.toJSON(payload);
    }

    public void setCategory(ECategory mainCat, String subCat) {
        this.category = mainCat.toString() + ";" + subCat;
    }

    // converts the payload using the PayloadConverter class and forwards the class which should be instantiated
    public ISendable getSendable(Class<? extends ISendable> type) {
        return PayloadConverter.fromJSON(payload, type);
    }

    public ECategory getMainCategory() {
        String mainCat = category.split(";")[0];
        if (mainCat.contains(";")) {
            log.error("Invalid message category: " + category);
        }
        return ECategory.valueOf(mainCat);
    }

    public String getSubCategory() {
        String subCat = category.split(";")[1];
        if (subCat.contains(";")) {
            log.error("Invalid message category: " + category);
        }
        return subCat;
    }
}
