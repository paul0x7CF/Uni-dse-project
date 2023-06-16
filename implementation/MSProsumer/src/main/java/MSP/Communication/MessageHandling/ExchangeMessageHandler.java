package MSP.Communication.MessageHandling;

import CF.sendable.MSData;
import CF.sendable.Transaction;
import MSP.Communication.callback.CallbackTransaction;
import MSP.Exceptions.MessageNotSupportedException;
import MSP.Exceptions.TransactionInvalidException;
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
    private BlockingQueue<TimeSlot> availableTimeSlots;
    private CallbackTransaction callbackOnTransaction;
    private MSData myMSData;

    public ExchangeMessageHandler(BlockingQueue<TimeSlot> availableTimeSlots, CallbackTransaction callbackOnTransaction, MSData myMSData) {
        this.availableTimeSlots = availableTimeSlots;
        this.callbackOnTransaction = callbackOnTransaction;
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
            logger.warn(e.getMessage()+"Subcategory" + message.getSubCategory() + "not supported");
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

    private void handleTransaction(Message message) {
        logger.debug("Transaction message received");
        Transaction newTransaction = (Transaction) message.getSendable(Transaction.class);
        if (validateTransaction(newTransaction)) {
            double transactionPrice = calculateTransactionPrice(newTransaction);
            logger.debug("Call callbackOnTransaction with price: {}", transactionPrice);
            this.callbackOnTransaction.callback(transactionPrice);
        }
        else {
            logger.warn("Transaction was Ignored");
        }

    }

    private boolean validateTransaction(Transaction transactionToValidate) {
        if (transactionToValidate.getBuyerID().equals(this.myMSData.getId()) || transactionToValidate.getSellerID().equals(this.myMSData.getId())) {
            logger.trace("Transaction is for Me");
            return true;
        } else {
            try {
                throw new TransactionInvalidException("Received Transaction has no corresponding ID  to this MS");
            } catch (TransactionInvalidException e) {
                logger.warn(e.getMessage());
            }
            return false;
        }
    }

    private double calculateTransactionPrice(Transaction transactionToCalculate) {
        double resultPrice = 0;
        if (transactionToCalculate.getBuyerID().equals(this.myMSData.getId())) {
            resultPrice = transactionToCalculate.getPrice() * transactionToCalculate.getAmount();
        } else {
            resultPrice = transactionToCalculate.getPrice() * transactionToCalculate.getAmount() * -1;
        }
        return resultPrice;
    }
}
