package CF.messageHandling;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.protocol.ECategory;
import CF.protocol.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * MessageHandler class that handles messages by calling the appropriate handler. It is used to handle messages
 * received by the broker.
 * <p>
 * Before handling messages the user has to add a message handler for every category a message can have.
 * <p>
 * The message handler for the Info category is added by default by the broker, as it is the same for every component.
 * <p>
 * The MessageHandler has a Map<ECategory, IMessageHandler> which maps the category of a message to the appropriate
 * handler.
 *
 * @see IMessageHandler
 */
public class MessageHandler implements IMessageHandler {
    private final Map<ECategory, IMessageHandler> handlers = new HashMap<>();

    public MessageHandler() {
    }

    /**
     * Handles the message by calling the appropriate handler. Could be done within this method, but I wanted to keep the
     * error handling separate to give an overview of the different message categories.
     *
     * @param message   The message to handle
     *
     * @throws MessageProcessingException  If the message category is unknown
     */
    public void handleMessage(Message message) throws MessageProcessingException {
        if (!handlers.containsKey(message.getMainCategory())) {
            throw new MessageProcessingException("No handler found for message: " + message);
        }
        switch (message.getMainCategory()) {
            case Info -> handleInfo(message);
            case Auction -> handleAuction(message);
            case Exchange -> handleExchange(message);
            case Forecast -> handleForecast(message);
            default ->
                    throw new MessageProcessingException("Unknown message mainCategory: " + message.getMainCategory());
        }
    }

    public void addMessageHandler(ECategory category, IMessageHandler handler) {
        if (category == ECategory.Info && handlers.containsKey(category)) {
            throw new IllegalArgumentException("Cannot override InfoMessageHandler");
        }
        handlers.put(category, handler);
    }

    private void handleInfo(Message message) throws MessageProcessingException {
        try {
            handlers.get(ECategory.Info).handleMessage(message);
        } catch (RemoteException e) {
            throw new MessageProcessingException("RemoteException: " + e.getMessage());
        }
    }

    private void handleAuction(Message message) throws MessageProcessingException {
        try {
            handlers.get(ECategory.Auction).handleMessage(message);
        } catch (MessageProcessingException | RemoteException e) {
            throw new MessageProcessingException("No handler found for message: " + message);
        }
    }

    private void handleExchange(Message message) throws MessageProcessingException {
        try {
            handlers.get(ECategory.Exchange).handleMessage(message);
        } catch (MessageProcessingException | RemoteException e) {
            throw new MessageProcessingException("No handler found for message: " + message);
        }
    }

    private void handleForecast(Message message) throws MessageProcessingException {
        try {
            handlers.get(ECategory.Forecast).handleMessage(message);
        } catch (MessageProcessingException | RemoteException e) {
            throw new MessageProcessingException("No handler found for message: " + message);
        }
    }
}
