package MSF.communication;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.ConsumptionResponse;
import CF.sendable.MSData;
import CF.sendable.SolarResponse;

import java.util.UUID;

public class ForecastMessageBuilder {
    private MSData myMSData;

    public ForecastMessageBuilder() {
    }

    public Message buildSolarResponseMessage(SolarResponse solarResponse, String senderAddress, int senderPort, UUID senderID) {
        MessageFactory productionMessageFactory = senderAndReceiverTemplate(senderAddress, senderPort, senderID);
        productionMessageFactory.
                setCategory(ECategory.Forecast, "Production").
                setPayload(solarResponse);
        return productionMessageFactory.build();
    }

    public Message buildConsumptionResponseMessage(ConsumptionResponse consumptionResponse, String senderAddress, int senderPort, UUID senderID) {
        MessageFactory consumptionMessageFactory = senderAndReceiverTemplate(senderAddress, senderPort, senderID);
        consumptionMessageFactory.
                setCategory(ECategory.Forecast, "Consumption").
                setPayload(consumptionResponse);
        return consumptionMessageFactory.build();
    }

    private MessageFactory senderAndReceiverTemplate(String senderAddress, int senderPort, UUID senderID) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(myMSData.getId()).
                setSenderAddress(myMSData.getAddress()).
                setSenderPort(myMSData.getPort()).
                setReceiverID(senderID).
                setReceiverAddress(senderAddress).
                setReceiverPort(senderPort);
    }

    public void setMyMSData(MSData myMSData) {
        this.myMSData = myMSData;
    }
}
