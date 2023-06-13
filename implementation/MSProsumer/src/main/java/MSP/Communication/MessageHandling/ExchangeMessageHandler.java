package MSP.Communication.MessageHandling;

import MSP.Exceptions.MessageNotSupportedException;
import MSP.Logic.Prosumer.Prosumer;
import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import CF.sendable.TimeSlot;

import java.util.concurrent.BlockingQueue;

public class ExchangeMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);
    private Prosumer myProsumer;
    private BlockingQueue<TimeSlot> availableTimeSlots;

    public ExchangeMessageHandler(Prosumer prosumer, BlockingQueue<TimeSlot> availableTimeSlots) {
        this.myProsumer = prosumer;
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
        logger.debug("TimeSlot message received adding to BlockingQueue");
        TimeSlot newTimeSlot = (TimeSlot) message.getSendable(TimeSlot.class);
        try {
            this.availableTimeSlots.put(newTimeSlot);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
