package messageHandling;

import exceptions.MessageProcessingException;
import protocol.ECategory;
import protocol.Message;

import java.util.Map;

public class MessageHandler implements IMessageHandler {
    private Map<ECategory, IMessageHandler> handlers;

    public void handleMessage(Message message) throws MessageProcessingException {
        switch(message.getMainCategory()) {
            case Info -> handleInfo(message);
            case Auction -> handleAuction(message);
            case Transaction -> handleTransaction(message);
            case Forecast -> handleForecast(message);
            default -> throw new MessageProcessingException("Unknown message mainCategory: " + message.getMainCategory());
        }
    }

    public void addMessageHandler(ECategory category, IMessageHandler handler) {
        handlers.put(category, handler);
    }

    private void handleInfo(Message message) {
        try {
            handlers.get(ECategory.Info).handleMessage(message);
        } catch (MessageProcessingException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    private void handleAuction(Message message) {
        try {
            handlers.get(ECategory.Auction).handleMessage(message);
        } catch (MessageProcessingException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    private void handleTransaction(Message message) {
        try {
            handlers.get(ECategory.Transaction).handleMessage(message);
        } catch (MessageProcessingException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    private void handleForecast(Message message) {
        try {
            handlers.get(ECategory.Forecast).handleMessage(message);
        } catch (MessageProcessingException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }
}
