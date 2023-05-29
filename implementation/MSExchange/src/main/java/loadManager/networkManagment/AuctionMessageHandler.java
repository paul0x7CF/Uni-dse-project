package loadManager.networkManagment;

import broker.IServiceBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;
import sendable.MSData;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class AuctionMessageHandler implements IMessageHandler {
    private final Logger logger = Logger.getLogger(AuctionMessageHandler.class.getName());
    private final MSData currentService;
    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;
    private IServiceBroker broker;

    public AuctionMessageHandler(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, IServiceBroker broker) {
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        this.broker = broker;
        this.currentService = broker.getCurrentService();
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}
