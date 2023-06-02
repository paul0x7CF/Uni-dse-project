package loadManager.networkManagment;

import mainPackage.BaseCommunication;
import msExchange.networkCommunication.IncomingMessageHandler;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;

import java.util.concurrent.BlockingQueue;

public class CommunicationLoadManager extends BaseCommunication {
    private static final String PROPERTIES_FILE_PATH = "C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties";
    private static final EServiceType EXCHANGE_TYPE = EServiceType.Exchange;

    public CommunicationLoadManager(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        super(incomingMessages, PROPERTIES_FILE_PATH, EXCHANGE_TYPE);
    }

    public void addMessageHandler(ECategory category) {
        switch (category) {
            case Auction -> {
                super.addMessageHandler(category, new IncomingMessageHandler(this.incomingMessages));
            }
            case Exchange -> {
                super.addMessageHandler(category, new ExchangeMessageHandler(this.incomingMessages));
            }
            default -> {
                throw new RuntimeException("Category not supported");
            }
        }
    }
}
