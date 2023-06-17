package MSF.communication.messageHandler;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.sendable.ConsumptionRequest;
import CF.sendable.SolarRequest;
import MSF.data.EProsumerRequestType;
import MSF.data.ProsumerConsumptionRequest;
import MSF.data.ProsumerSolarRequest;
import MSF.exceptions.MessageNotSupportedException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import java.util.concurrent.BlockingQueue;

public class ProsumerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ProsumerMessageHandler.class);
    private final BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest;
    private final BlockingQueue<ProsumerSolarRequest> incomingSolarRequest;

    public ProsumerMessageHandler(BlockingQueue<ProsumerConsumptionRequest> incomingConsumptionRequest, BlockingQueue<ProsumerSolarRequest> incomingSolarRequest) {
        this.incomingConsumptionRequest = incomingConsumptionRequest;
        this.incomingSolarRequest = incomingSolarRequest;
    }

    @Override
    public void handleMessage(Message message) {
        try {
            switch (message.getSubCategory()) {
                case "Production" -> handleProduction(message);
                case "Consumption" -> handleConsumption(message);
                default -> throw new MessageNotSupportedException();
            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }
    }

    private void handleConsumption(Message message) {
        logger.trace("Consumption message received with ID: " + message.getMessageID());

        ConsumptionRequest consumptionRequest = (ConsumptionRequest) message.getSendable(ConsumptionRequest.class);
        ProsumerConsumptionRequest request = new ProsumerConsumptionRequest(EProsumerRequestType.CONSUMPTION,
                consumptionRequest.getConsumptionMap(),
                consumptionRequest.getRequestTimeSlotId(),
                message.getSenderAddress(),
                message.getSenderPort(),
                message.getSenderID());

        try {
            this.incomingConsumptionRequest.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleProduction(Message message) {
        logger.trace("Production message received with ID: " + message.getMessageID());

        SolarRequest solarRequest = (SolarRequest) message.getSendable(SolarRequest.class);

        ProsumerSolarRequest request = new ProsumerSolarRequest(
                solarRequest.getAmountOfPanels(),
                solarRequest.getArea(),
                solarRequest.getCompassAngle(),
                solarRequest.getStandingAngle(),
                solarRequest.getEfficiency(),
                solarRequest.getRequestTimeSlotId(),
                message.getSenderAddress(),
                message.getSenderPort(),
                message.getSenderID());
        try {
            this.incomingSolarRequest.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
