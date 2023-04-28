package messageHandling;

import protocol.Message;

public abstract class AForecastMessageHandler implements IMessageHandler {
    protected abstract void handleAuction(Message message);
}