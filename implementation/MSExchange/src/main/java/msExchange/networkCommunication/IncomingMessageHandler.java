package msExchange.networkCommunication;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

import java.util.concurrent.BlockingQueue;

public class IncomingMessageHandler implements IMessageHandler {
    private BlockingQueue<Message> incomingMessages;

    public IncomingMessageHandler(BlockingQueue<Message> incomingMessages) {
        this.incomingMessages = incomingMessages;
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        incomingMessages.add(message);
    }
}
