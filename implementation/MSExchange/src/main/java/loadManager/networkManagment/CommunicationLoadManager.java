package loadManager.networkManagment;

import mainPackage.BaseCommunication;
import mainPackage.IncomingMessageHandler;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;

import java.util.concurrent.BlockingQueue;

public class CommunicationLoadManager extends BaseCommunication {
    private static final EServiceType EXCHANGE_TYPE = EServiceType.Exchange;

    public CommunicationLoadManager(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        super(incomingMessages, EXCHANGE_TYPE, 0);
        addMessageHandler();
    }

    public void addMessageHandler() {
        super.addMessageHandler(ECategory.Auction, new IncomingMessageHandler(this.incomingMessages));
        super.addMessageHandler(ECategory.Exchange, new IncomingMessageHandler(this.incomingMessages));
    }
}
