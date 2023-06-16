package MSS.communication.messageHandling;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import CF.protocol.Message;
import CF.sendable.MSData;
import CF.sendable.TimeSlot;
import CF.sendable.Transaction;

import MSS.exceptions.MessageNotSupportedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ExchangeMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(ExchangeMessageHandler.class);

    private final MSData myMSData;

    public ExchangeMessageHandler(MSData myMSData) {
        this.myMSData = myMSData;
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        try {
            switch (message.getSubCategory()) {
                case "TimeSlot" -> handleTimeSlot(message);
                case "Transaction" -> handleTransaction(message);
                default -> throw new MessageNotSupportedException();
            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }

    }

    private void handleTimeSlot(Message message) {
        logger.debug("TimeSlot message received adding to BlockingQueue");
        TimeSlot newTimeSlot = (TimeSlot) message.getSendable(TimeSlot.class);


    }

    private void handleTransaction(Message message) {
        logger.debug("Transaction message received");
        Transaction newTransaction = (Transaction) message.getSendable(Transaction.class);


    }
}
