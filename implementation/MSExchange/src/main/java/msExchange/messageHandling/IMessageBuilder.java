package msExchange.messageHandling;

import protocol.Message;
import sendable.Bid;

public interface IMessageBuilder {
    public Message buildMessagePriceIsToLos(Bid bid, double averagePrice);
}
