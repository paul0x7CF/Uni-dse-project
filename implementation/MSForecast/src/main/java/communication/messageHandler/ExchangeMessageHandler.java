package communication.messageHandler;

import exceptions.MessageNotSupportedException;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.TimeSlot;

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
