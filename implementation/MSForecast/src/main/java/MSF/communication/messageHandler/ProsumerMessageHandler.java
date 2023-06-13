package MSF.communication.messageHandler;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.sendable.ConsumptionRequest;
import CF.sendable.SolarRequest;
import MSF.data.EProsumerRequestType;
import MSF.data.ProsumerRequest;
import MSF.exceptions.MessageNotSupportedException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import java.util.concurrent.BlockingQueue;

public class ProsumerMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ProsumerMessageHandler.class);
    private BlockingQueue<ProsumerRequest> incomingRequest;

    public ProsumerMessageHandler(BlockingQueue<ProsumerRequest> incomingRequest) {
        this.incomingRequest = incomingRequest;
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
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
        logger.trace("Consumption message received");

        ConsumptionRequest consumptionRequest = (ConsumptionRequest) message.getSendable(ConsumptionRequest.class);
        ProsumerRequest request = new ProsumerRequest(EProsumerRequestType.CONSUMPTION, consumptionRequest.getConsumptionMap(), consumptionRequest.getRequestTimeSlotId());

        try {
            this.incomingRequest.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleProduction(Message message) {
        logger.trace("Production message received");

        SolarRequest solarRequest = (SolarRequest) message.getSendable(SolarRequest.class);
        ProsumerRequest request = new ProsumerRequest(EProsumerRequestType.PRODUCTION, solarRequest.getArea(), solarRequest.getAngle(), solarRequest.getEfficiency());

        try {
            this.incomingRequest.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
