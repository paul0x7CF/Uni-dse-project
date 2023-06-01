package loadManager.networkManagment;

import broker.IServiceBroker;
import mainPackage.BaseCommunication;
import mainPackage.EExchangeType;
import msExchange.networkCommunication.IncomingMessageHandler;
import protocol.ECategory;
import protocol.Message;

import java.util.concurrent.BlockingQueue;

public class CommunicationLoadManager extends BaseCommunication {
    private static final String PROPERTIES_FILE_PATH = "C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties";
    private static final EExchangeType EXCHANGE_TYPE = EExchangeType.LoadManager;

    public CommunicationLoadManager(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        super(incomingMessages, outgoingMessages, PROPERTIES_FILE_PATH, EXCHANGE_TYPE);
    }

    public void addMessageHandler(ECategory category) {
        switch (category) {
            case Auction -> {
                super.addMessageHandler(category, new IncomingMessageHandler(this.incomingMessages));
            }
            case Exchange -> {
                super.addMessageHandler(category, new ExchangeMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) communicationBroker));
            }
            default -> {
                throw new RuntimeException("Category not supported");
            }
        }
    }
}
