package MSP.Communication.MessageHandling;

import MSP.Exceptions.MessageNotSupportedException;
import MSP.Logic.Prosumer.Prosumer;
import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;

public class ForecastMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ForecastMessageHandler.class);

    Prosumer myProsumer;

    public ForecastMessageHandler(Prosumer prosumer) {
        this.myProsumer = prosumer;

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
    }

    private void handleProduction(Message message) {
        logger.trace("Production message received");
    }
}
