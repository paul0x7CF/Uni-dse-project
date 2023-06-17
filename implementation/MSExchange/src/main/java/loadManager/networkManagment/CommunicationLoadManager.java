package loadManager.networkManagment;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import mainPackage.networkHelper.BaseCommunication;
import mainPackage.networkHelper.IncomingMessageHandler;

import java.util.concurrent.BlockingQueue;

public class CommunicationLoadManager extends BaseCommunication {
    private static final EServiceType EXCHANGE_TYPE = EServiceType.Exchange;

    public CommunicationLoadManager(BlockingQueue<Message> incomingMessages) {
        super(incomingMessages, EXCHANGE_TYPE, 0);
        addMessageHandler();
    }

    public void addMessageHandler() {
        super.addMessageHandler(ECategory.Auction, new IncomingMessageHandler(this.incomingMessages));
        super.addMessageHandler(ECategory.Exchange, new IncomingMessageHandler(this.incomingMessages));
    }
}
