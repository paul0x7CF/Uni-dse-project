package loadManager.networkManagment;

import loadManager.exchangeManagement.ExchangeServiceInformation;
import protocol.Message;
import protocol.MessageBuilder;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;

public class ExtendedMessageBuilder {
    private Message buildMessagePriceIsToLow(Bid bid, double averagePrice) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessagePriceIsToHigh(Sell sell, double averagePrice) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageSendTimeSlot(TimeSlot timeSlot) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageSendBid(Bid bid, ExchangeServiceInformation exchangeServiceInformation) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageForUnsatisfiedBidder(Bid bid) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageForUnsatisfiedSeller(Sell sell) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageSendSell(Sell sell, ExchangeServiceInformation exchangeServiceInformation) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

    private Message buildMessageTransactionForUnsatisfiedSeller(Sell sell) {
        MessageBuilder messageBuilder = new MessageBuilder();
        return null;
    }

}
