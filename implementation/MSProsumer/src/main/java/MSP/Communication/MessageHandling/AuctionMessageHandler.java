package MSP.Communication.MessageHandling;

import MSP.Exceptions.MessageNotSupportedException;
import MSP.Logic.Prosumer.Prosumer;
import CF.broker.IBroker;
import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;

public class AuctionMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(AuctionMessageHandler.class);
    private IBroker broker;
    private Prosumer myProsumer;
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
