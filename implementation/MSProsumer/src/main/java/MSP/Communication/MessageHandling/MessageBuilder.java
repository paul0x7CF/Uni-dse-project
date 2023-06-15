package MSP.Communication.MessageHandling;

import CF.sendable.*;
import MSP.Data.ESubCategory;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageBuilder {

    private static final Logger logger = LogManager.getLogger(MessageBuilder.class);

    private final MSData sender;

    public MessageBuilder(MSData myMSData) {
        this.sender = myMSData;
    }

    public Message buildConsumptionForecastMessage(ConsumptionRequest consumptionRequest, MSData receiver) {
        ECategory messageCategory = ECategory.Forecast;
        ESubCategory messageSubCategory = ESubCategory.Consumption;
        logger.trace("Building message with category: {} and subcategory: {}", messageCategory, messageSubCategory);
        MessageFactory consumptionMessageFactory = messageTemplate(receiver, messageCategory, messageSubCategory);
        consumptionMessageFactory.setPayload(consumptionRequest);
        return consumptionMessageFactory.build();
    }

    public Message buildProductionForecastMessage(SolarRequest solarRequest, MSData receiver) {
        ECategory messageCategory = ECategory.Forecast;
        ESubCategory messageSubCategory = ESubCategory.Production;
        logger.trace("Building message with category: {} and subcategory: {}", messageCategory, messageSubCategory);
        MessageFactory productionMessageFactory = messageTemplate(receiver, messageCategory, messageSubCategory);
        productionMessageFactory.setPayload(solarRequest);
        return productionMessageFactory.build();
    }


    public Message buildBidMessage(Bid bidToSend, MSData receiver) {
        ECategory messageCategory = ECategory.Auction;
        ESubCategory messageSubCategory = ESubCategory.Bid;
        logger.trace("Building message with category: {} and subcategory: {}", messageCategory, messageSubCategory);
        MessageFactory bidMessageFactory = messageTemplate(receiver, messageCategory, messageSubCategory);
        bidMessageFactory.setPayload(bidToSend);
        return bidMessageFactory.build();
    }

    public Message buildSellMessage(Sell sellToSend, MSData receiver) {
        ECategory messageCategory = ECategory.Auction;
        ESubCategory messageSubCategory = ESubCategory.Sell;
        logger.trace("Building message with category: {} and subcategory: {}", messageCategory, messageSubCategory);
        MessageFactory bidMessageFactory = messageTemplate(receiver, messageCategory, messageSubCategory);
        bidMessageFactory.setPayload(sellToSend);
        return bidMessageFactory.build();
    }

    private MessageFactory messageTemplate(MSData receiver, ECategory messageCategory, ESubCategory messageSubCategory) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(sender.getId()).
                setSenderAddress(sender.getAddress()).
                setSenderPort(sender.getPort()).
                setReceiverID(receiver.getId()).
                setReceiverAddress(receiver.getAddress()).
                setReceiverPort(receiver.getPort()).
                setCategory(messageCategory, messageSubCategory.toString());
    }

}
