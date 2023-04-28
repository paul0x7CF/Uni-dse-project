package messageHandling;

import protocol.Message;

public abstract class AAuctionMessageHandler implements IMessageHandler {
    protected abstract void handleAuction(Message message);
}
