package msExchange.networkCommunication;

import mainPackage.BaseCommunication;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;

import java.util.concurrent.BlockingQueue;

public class CommunicationExchange extends BaseCommunication {
    private static final String PROPERTIES_FILE_PATH = "C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties";
    private static final EServiceType SERVICE_TYPE = EServiceType.ExchangeWorker;

    public CommunicationExchange(BlockingQueue<Message> incomingMessages) {
        super(incomingMessages, PROPERTIES_FILE_PATH, SERVICE_TYPE);
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
