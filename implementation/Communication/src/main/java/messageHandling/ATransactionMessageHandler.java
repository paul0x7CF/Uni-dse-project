package messageHandling;

import protocol.Message;

public abstract class ATransactionMessageHandler implements IMessageHandler {
    protected abstract void handleAuction(Message message);
}
