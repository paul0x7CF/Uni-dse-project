package MSProsumer.Communication;

import messageHandling.MessageHandler;
import org.apache.logging.log4j.message.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.SolarRequest;

public class ExtendedMessageHandler extends MessageHandler {

    public Message buildBidMessage(Bid bid) {
        return null;
    }

    public Message buildSellMessage(Sell sell) {
        return null;
    }

    public Message buildAskSolarPredictionMessage(SolarRequest solarpanel) {
        return null;
    }
}
