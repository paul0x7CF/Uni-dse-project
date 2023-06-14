package MSP.Communication.MessageHandling;

import MSP.Data.ESubCategory;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.ConsumptionRequest;
import CF.sendable.MSData;
import CF.sendable.SolarRequest;

public class MessageBuilder {

    private final MSData sender;

    public MessageBuilder(MSData myMSData) {
        this.sender = myMSData;
    }

    public Message buildConsumptionForecastMessage(ConsumptionRequest consumptionRequest, MSData receiver){
        MessageFactory consumptionMessageFactory = senderAndReceiverTemplate(receiver);
        consumptionMessageFactory.
                setCategory(ECategory.Forecast, ESubCategory.Consumption.toString()).
                setPayload(consumptionRequest);
        return consumptionMessageFactory.build();
    }
    public Message buildProductionForecastMessage(SolarRequest solarRequest, MSData receiver) {
        MessageFactory productionMessageFactory = senderAndReceiverTemplate(receiver);
        productionMessageFactory.
                setCategory(ECategory.Forecast, ESubCategory.Production.toString()).
                setPayload(solarRequest);
        return productionMessageFactory.build();

    }

    private MessageFactory senderAndReceiverTemplate(MSData receiver) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(sender.getId()).
                setSenderAddress(sender.getAddress()).
                setSenderPort(sender.getPort()).
                setReceiverID(receiver.getId()).
                setReceiverAddress(receiver.getAddress()).
                setReceiverPort(receiver.getPort());
    }

}
