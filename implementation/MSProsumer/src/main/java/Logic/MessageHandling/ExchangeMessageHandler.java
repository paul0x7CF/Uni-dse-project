package Logic.MessageHandling;

import Exceptions.MessageNotSupportedException;
import Logic.Prosumer.Prosumer;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;

public class ExchangeMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        try {
            switch (message.getSubCategory()) {
                case "TimeSlot" -> handleTimeSlot(message);
                default -> throw new MessageNotSupportedException();
            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }

    }

    private void handleTimeSlot(Message message) {
        logger.trace("TimeSlot message received");
    }
}
