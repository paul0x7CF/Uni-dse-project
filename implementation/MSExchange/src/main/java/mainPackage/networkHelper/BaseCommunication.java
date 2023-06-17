package mainPackage.networkHelper;

import CF.broker.BrokerRunner;
import CF.communication.NetworkHandler;
import CF.mainPackage.ConfigReader;
import CF.messageHandling.IMessageHandler;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import mainPackage.PropertyFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public abstract class BaseCommunication {
    private static final Logger logger = LogManager.getLogger(BaseCommunication.class.getName());
    protected BrokerRunner communicationBroker;
    protected NetworkHandler networkHandler;
    protected BlockingQueue<Message> incomingMessages;


    public BaseCommunication(BlockingQueue<Message> incomingMessages, EServiceType exchangeType, int instanceNumber) {
        //read Properties
        ConfigReader configReader = new ConfigReader();
        PropertyFileReader properties = new PropertyFileReader();

        int PORT = Integer.parseInt(configReader.getProperty("exchangePort"));
        EServiceType serviceType;

        switch (exchangeType) {
            case ExchangeWorker -> {
                PORT = PORT + Integer.parseInt(configReader.getProperty("portJumpSize")) * instanceNumber;
                logger.debug("new Port: {}", PORT);
                serviceType = EServiceType.valueOf(properties.getExchangeServiceType());
            }
            case Exchange -> {
                serviceType = EServiceType.valueOf(properties.getLoadManagerServiceType());
            }
            default -> throw new IllegalStateException("Unexpected value: " + exchangeType);
        }


        this.incomingMessages = incomingMessages;
        createBroker(PORT, serviceType);

        logger.debug("COMMUNICATION: MS registered with Id: {}, Address: {}, Port: {}" , this.communicationBroker.getCurrentService().getId(), this.communicationBroker.getCurrentService().getAddress(), this.communicationBroker.getCurrentService().getPort());
    }

    private void createBroker(int port, EServiceType serviceType) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
    }

    public void startBrokerRunner() {
        this.communicationBroker.run();
    }

    public void sendMessage(Message message) {
        communicationBroker.sendMessage(message);
    }

    public void addMessageHandler(ECategory category, IMessageHandler handler) {
        try {
            this.communicationBroker.addMessageHandler(category, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BrokerRunner getBroker() {
        return communicationBroker;
    }


}
