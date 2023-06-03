package Communication.MessageHandling;

import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.ConsumptionRequest;
import sendable.MSData;

public class MessageBuilder {

    private MSData sender;

    public MessageBuilder() {
        //TODO: implement constructor
        //Maybe add broker here?
        //Maybe only some types of broker?
    }

    public Message buildConsumptionForecastMessage(ConsumptionRequest consumptionRequest, MSData receiver){
        //TODO: implement logic to build consumption forecast message
        MessageFactory consumptionMessageFactory = senderAndReceiverTemplate(receiver);
        consumptionMessageFactory.
                setCategory(ECategory.Forecast, "Consumption").
                setPayload(consumptionRequest);
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
