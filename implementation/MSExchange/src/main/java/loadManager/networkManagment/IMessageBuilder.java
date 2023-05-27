package loadManager.networkManagment;

import loadManager.SellInformation;
import protocol.ECategory;
import protocol.ESubCategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public interface IMessageBuilder {
    private static MSData buildMSData() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("../config.properties");
            properties.load(configFile);
            configFile.close();

            int PORT = Integer.parseInt(properties.getProperty("loadmanager.port"));
            String ADDRESS = properties.getProperty("loadmanager.address");
            EServiceType SERVICE_TYPE = EServiceType.valueOf(properties.getProperty("loadmanager.serviceType"));
            UUID id = UUID.fromString(properties.getProperty("loadmanager.id"));
            return new MSData(id, SERVICE_TYPE, ADDRESS, PORT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to build a message containing a bid object, with the average price
     *
     * @param bid
     * @param averagePrice
     * @return
     */
    public static Message buildMessagePriceIsToLow(Bid bid, double averagePrice) {
        //TODO: where do i get the receiver MSDATA?
        //just for now -> change later to correct MSData
        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);

        Bid editedBid = new Bid(bid.getVolume(), averagePrice, bid.getTimeSlot(), bid.getBidderID());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(editedBid);

        return messageFactory.build();
    }

    /**
     * This method is used to build a message Factory consisting of the MSData from sender and receiver with the
     * following template:
     *
     * @param receiver
     * @return
     */
    public static MessageFactory senderAndReceiverTemplate(MSData receiver) {
        MessageFactory messageFactory = new MessageFactory();
        MSData sender = buildMSData();
        return messageFactory.setSenderID(sender.getId())
                .setSenderAddress(sender.getAddress())
                .setSenderPort(sender.getPort())
                .setReceiverID(receiver.getId())
                .setReceiverAddress(receiver.getAddress())
                .setReceiverPort(receiver.getPort());
    }

    /**
     * This method is used to build a message containing a sell object, with the average price
     *
     * @param sell
     * @param averagePrice
     * @return
     */
    public static Message buildMessagePriceIsToHigh(Sell sell, double averagePrice) {
        //TODO: where do i get the receiver MSDATA?
        //just for now -> change later to correct MSData
        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);

        Sell editedSell = new Sell(sell.getVolume(), averagePrice, sell.getTimeSlot(), sell.getSellerID());
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(editedSell);

        return messageFactory.build();
    }

    /**
     * Message for the exchange with the right auction -> TODO: where to get the exchange id?
     *
     * @param bid
     * @return
     */
    public static Message buildMessageSendBid(Bid bid) {
        //TODO: where do i get the receiver MSDATA?
        //just for now -> change later to correct MSData

        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);

        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);

        return messageFactory.build();
    }

    /**
     * This method is used to build a message containing a bid object going to the storage to be handled there
     *
     * @param bid
     * @return
     */
    public static Message buildMessageForUnsatisfiedBidder(Bid bid, UUID storageID) {
        //TODO: where do I get the receiver MSDATA?
        //just for now -> change later to correct MSData

        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);

        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);

        return messageFactory.build();
    }

    /**
     * sending message to exchange service
     *
     * @param sellInformation
     * @return
     */
    public static Message buildMessageForSell(SellInformation sellInformation) {
        //TODO: where do I get the receiver MSDATA?
        //just for now -> change later to correct MSData
        UUID exchangeService = sellInformation.getExchangeID();

        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);


        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sellInformation.getSell());
        return messageFactory.build();
    }

    /**
     * This method is used to build a message containing a sell object going to the storage to be handled there
     *
     * @param sell
     * @param storageID
     * @return
     */
    public static Message buildMessageForUnsatisfiedSeller(Sell sell, UUID storageID) {
        //TODO: where do I get the receiver MSDATA?
        //just for now -> change later to correct MSData

        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = senderAndReceiverTemplate(receiverMS);

        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Sell)).setPayload(sell);
        return messageFactory.build();
    }

    public static Message buildTransactionMessage(Transaction transaction) {
        //TODO: As broadcast -> what is the MS?
        /*
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Transaction)).setPayload(transaction);
        return messageFactory.build();*/

        return null;
    }

    public Message buildTimeSlotMessage();
    
}
