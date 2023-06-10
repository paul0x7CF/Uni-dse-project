package mainPackage;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import CF.protocol.Message;

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
