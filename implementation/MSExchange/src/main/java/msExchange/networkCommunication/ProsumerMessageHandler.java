package msExchange.networkCommunication;

import broker.IServiceBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

import java.util.concurrent.BlockingQueue;

public class ProsumerMessageHandler implements IMessageHandler {
    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;
    private IServiceBroker communicationBroker;

    public ProsumerMessageHandler(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, IServiceBroker communicationBroker) {
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        this.communicationBroker = communicationBroker;
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}
