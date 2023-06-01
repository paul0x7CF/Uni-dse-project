package msExchange;

import broker.BrokerRunner;
import exceptions.MessageProcessingException;
import protocol.Message;

public class MessageBuilder {
    private BrokerRunner broker;

    public MessageBuilder(BrokerRunner broker) {
        this.broker = broker;
    }

    public static Message buildMessageToSendSellToExchange() {
        return null;
    }

    public void sendErrorMessage(Message message, MessageProcessingException e) {
        //TODO: implement
    }
}
