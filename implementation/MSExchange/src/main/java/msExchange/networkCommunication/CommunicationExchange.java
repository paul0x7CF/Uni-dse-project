package msExchange.networkCommunication;

import mainPackage.BaseCommunication;
import mainPackage.networkHelper.IncomingMessageHandler;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;

import java.util.concurrent.BlockingQueue;

public class CommunicationExchange extends BaseCommunication {
     private static final EServiceType SERVICE_TYPE = EServiceType.ExchangeWorker;

    public CommunicationExchange(BlockingQueue<Message> incomingMessages, int instanceNumber) {
        super(incomingMessages, SERVICE_TYPE, instanceNumber);
        addMessageHandler();
    }

    /**
     * Adds the message handler for Category Auction and Exchange. The incoming messages will be added to BlockingQueue,
     * which will be handled in Exchange
     */
    private void addMessageHandler() {
        super.addMessageHandler(ECategory.Auction, new IncomingMessageHandler(this.incomingMessages));
        super.addMessageHandler(ECategory.Exchange, new IncomingMessageHandler(this.incomingMessages));
    }
}
