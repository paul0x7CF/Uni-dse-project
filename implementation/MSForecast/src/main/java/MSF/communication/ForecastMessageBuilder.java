package MSF.communication;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.ConsumptionResponse;
import CF.sendable.MSData;
import CF.sendable.SolarResponse;

public class ForecastMessageBuilder {
    private MSData sender;

    public ForecastMessageBuilder() {
        //TODO: implement constructor
        //Maybe add broker here?
        //Maybe only some types of broker?
    }

    public Message buildSolarProductionMessage(SolarResponse solarResponse, MSData receiver) {
        MessageFactory productionMessageFactory = senderAndReceiverTemplate(receiver);
        productionMessageFactory.
                setCategory(ECategory.Forecast, "Production").
                setPayload(solarResponse);
        return productionMessageFactory.build();
    }

    public Message buildConsumerConsumptionMessage(ConsumptionResponse consumptionResponse, MSData receiver) {
        MessageFactory consumptionMessageFactory = senderAndReceiverTemplate(receiver);
        consumptionMessageFactory.
                setCategory(ECategory.Forecast, "Consumption").
                setPayload(consumptionResponse);
        return consumptionMessageFactory.build();
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
