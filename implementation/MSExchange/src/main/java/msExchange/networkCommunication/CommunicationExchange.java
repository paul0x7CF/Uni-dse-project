package msExchange.networkCommunication;

import mainPackage.BaseCommunication;
import mainPackage.EExchangeType;
import protocol.ECategory;
import protocol.Message;

import java.util.concurrent.BlockingQueue;

public class CommunicationExchange extends BaseCommunication {
    private static final String PROPERTIES_FILE_PATH = "C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties";
    private static final EExchangeType EXCHANGE_TYPE = EExchangeType.Exchange;

    public CommunicationExchange(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        super(incomingMessages, outgoingMessages, PROPERTIES_FILE_PATH, EXCHANGE_TYPE);
        addMessageHandler();
    }

    /**
     * Adds the message handler for Category Auction and Exchange.
     * The incoming messages will be added to BlockingQueue, which will be handled in Exchange
     */
    private void addMessageHandler() {
        super.addMessageHandler(ECategory.Auction, new IncomingMessageHandler(this.incomingMessages));
        super.addMessageHandler(ECategory.Exchange, new IncomingMessageHandler(this.incomingMessages));
    }
}
