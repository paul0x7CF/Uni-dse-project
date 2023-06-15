package MSP.Communication.MessageHandling;

import CF.sendable.Bid;
import CF.sendable.MSData;
import CF.sendable.Sell;
import MSP.Communication.callback.CallbackBidHigher;
import MSP.Communication.callback.CallbackSellLower;
import MSP.Exceptions.InvalidBidException;
import MSP.Exceptions.MessageNotSupportedException;
import MSP.Exceptions.SellInvalidException;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import CF.broker.IBroker;
import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;

public class AuctionMessageHandler implements IMessageHandler {

    private static final Logger logger = LogManager.getLogger(AuctionMessageHandler.class);

    private MSData myMSData;
    private CallbackBidHigher callbackOnBidHigher;
    private CallbackSellLower callbackOnSellLower;

    public AuctionMessageHandler(MSData myMSData, CallbackBidHigher callbackOnBidHigher, CallbackSellLower callbackOnSellLower) {
        this.myMSData = myMSData;
        this.callbackOnBidHigher = callbackOnBidHigher;
        this.callbackOnSellLower = callbackOnSellLower;
    }

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
        logger.debug("SellLower message received");
        Sell sellToChange = (Sell) message.getSendable(Sell.class);
        if(validateSell(sellToChange)){
            logger.debug("Call callback on SellLower");
            this.callbackOnSellLower.callback(sellToChange);
        }
    }

    private void handleBidHigher(Message message) {
        logger.debug("BidHigher message received");
        Bid bidToChange = (Bid) message.getSendable(Bid.class);
        if(validateBid(bidToChange)){
            logger.debug("Call callback on BidHigher");
            this.callbackOnBidHigher.callback(bidToChange);
        }
    }

    private boolean validateBid(Bid bidToChange) {
        if (bidToChange.getBidderID().equals(this.myMSData.getId())) {
            return true;
        } else {
            try {
                throw new InvalidBidException("The Received Bid has no corresponding ID to this MS");
            } catch (InvalidBidException e) {
                logger.warn(e.getMessage());
                return false;
            }
        }
    }

    private boolean validateSell(Sell sellToValidate) {
        if (sellToValidate.getSellerID().equals(this.myMSData.getId())) {
            return true;
        } else {
            try {
                throw new SellInvalidException("The Received Sell has no corresponding ID to this MS");
            } catch (SellInvalidException e) {
                logger.warn(e.getMessage());
                return false;
            }
        }
    }

}
