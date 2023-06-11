package MSF.communication.messageHandler;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import MSF.exceptions.MessageNotSupportedException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import CF.sendable.TimeSlot;
import java.util.concurrent.BlockingQueue;

public class ExchangeMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);
    private BlockingQueue<TimeSlot> availableTimeSlots;

    public ExchangeMessageHandler(BlockingQueue<TimeSlot> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

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
        logger.trace("TimeSlot message received adding to BlockingQueue");
        TimeSlot newTimeSlot = (TimeSlot) message.getSendable(TimeSlot.class);
        try {
            this.availableTimeSlots.put(newTimeSlot);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
