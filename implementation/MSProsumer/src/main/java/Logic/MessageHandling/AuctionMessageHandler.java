package Logic.MessageHandling;

import Exceptions.MessageNotSupportedException;
import Logic.Prosumer.Prosumer;
import broker.IBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;

public class AuctionMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(AuctionMessageHandler.class);
    private IBroker broker;
    private Prosumer prosumer;
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        try {
            switch (message.getSubCategory()) {
                case "BidHigher" -> handleBidHigher(message);
                case "SellLower" -> handleSellLower(message);
                default -> throw new MessageNotSupportedException();

            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }
    }

    private void handleSellLower(Message message) {
        logger.trace("SellLower message received");
    }

    private void handleBidHigher(Message message) {
        logger.trace("BidHigher message received");
    }


}
