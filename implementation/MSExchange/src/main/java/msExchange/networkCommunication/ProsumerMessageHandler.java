package msExchange.networkCommunication;

import broker.IServiceBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

import java.util.concurrent.BlockingQueue;

public class ProsumerMessageHandler implements IMessageHandler {
    public ProsumerMessageHandler(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, IServiceBroker communicationBroker) {
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}
