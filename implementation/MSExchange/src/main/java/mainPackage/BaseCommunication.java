package mainPackage;

import CF.mainPackage.ConfigReader;
import CF.broker.BrokerRunner;
import CF.communication.NetworkHandler;
import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;

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

        int port = Integer.parseInt(configReader.getProperty("exchangePort"));
        EServiceType serviceType;

        switch (exchangeType) {
            case ExchangeWorker -> {
                port = port + Integer.parseInt(configReader.getProperty("portJumpSize")) * instanceNumber;
                serviceType = EServiceType.valueOf(properties.getExchangeServiceType());
            }
            case Exchange -> {
                serviceType = EServiceType.valueOf(properties.getLoadManagerServiceType());
            }
            default -> throw new IllegalStateException("Unexpected value: " + exchangeType);
        }


        this.incomingMessages = incomingMessages;
        createBroker(port, serviceType);

        logger.info("MS registered with Id:" + this.communicationBroker.getCurrentService().getId() + ", Address: " + this.communicationBroker.getCurrentService().getAddress() + ", Port: " + this.communicationBroker.getCurrentService().getPort());
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
