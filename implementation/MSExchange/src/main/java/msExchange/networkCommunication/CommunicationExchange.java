package msExchange.networkCommunication;

import broker.IServiceBroker;
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
    }

    public void addMessageHandler(ECategory category) {
        switch (category) {
            case Auction -> {
                super.addMessageHandler(category, new ProsumerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker));
            }
            case Exchange -> {
                super.addMessageHandler(category, new LoadBalancerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker));
            }
            default -> {
                throw new RuntimeException("Category not supported");
            }
        }
    }
}
