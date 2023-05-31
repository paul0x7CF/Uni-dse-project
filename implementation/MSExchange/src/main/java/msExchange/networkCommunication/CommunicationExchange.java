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
    private ProsumerMessageHandler prosumerMessageHandler;
    private LoadBalancerMessageHandler loadBalancerMessageHandler;

    public CommunicationExchange(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        super(incomingMessages, outgoingMessages, PROPERTIES_FILE_PATH, EXCHANGE_TYPE);
        addMessageHandler();
    }

    //TODO: When/where to call?
    private void addMessageHandler() {
        prosumerMessageHandler = new ProsumerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker);
        super.addMessageHandler(ECategory.Auction, prosumerMessageHandler);

        loadBalancerMessageHandler = new LoadBalancerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker);
        super.addMessageHandler(ECategory.Exchange, loadBalancerMessageHandler);
    }
}
