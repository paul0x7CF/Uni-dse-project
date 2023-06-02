package msExchange.messageHandling;

import broker.BrokerRunner;
import exceptions.MessageProcessingException;
import protocol.Message;
import sendable.Transaction;

public class MessageBuilder {
    private BrokerRunner broker;

    public MessageBuilder(BrokerRunner broker) {
        this.broker = broker;
    }

    public void sendErrorMessage(Message message, MessageProcessingException e) {
        //TODO: implement
    }

    public void sendCapacityMessage() {
        //TODO: implement
    }

    public Message buildMessage(Transaction transaction) {
        //TODO: implement
        return null;
    }
}
