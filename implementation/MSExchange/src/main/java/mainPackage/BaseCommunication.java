package mainPackage;

import broker.BrokerRunner;
import communication.NetworkHandler;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public abstract class BaseCommunication {
    private static final Logger logger = LogManager.getLogger(BaseCommunication.class.getName());
    protected BrokerRunner communicationBroker;
    protected NetworkHandler networkHandler;
    protected BlockingQueue<Message> incomingMessages;
    protected BlockingQueue<Message> outgoingMessages;


    public BaseCommunication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages,
                             String propertiesFilePath, EServiceType exchangeType) {
        //read Properties
        Properties properties = new Properties();
        int port;
        EServiceType serviceType;

        try {
            FileInputStream configFile = new FileInputStream(propertiesFilePath);
            properties.load(configFile);
            configFile.close();

            //TODO: Where do I get the port?
            switch (exchangeType) {
                case ExchangeWorker -> {
                    port = Integer.parseInt(properties.getProperty("exchange.port"));
                    serviceType = EServiceType.valueOf(properties.getProperty("exchange.serviceType"));
                }
                case Exchange -> {
                    port = Integer.parseInt(properties.getProperty("loadManager.port"));
                    serviceType = EServiceType.valueOf(properties.getProperty("loadManager.serviceType"));
                }
                default -> throw new IllegalStateException("Unexpected value: " + exchangeType);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
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

    public MSData getMyMSData() {
        return this.communicationBroker.getCurrentService();
    }

    public List<MSData> getServices() {
        return this.communicationBroker.getServices();
    }

    public MSData getService(UUID id) {
        return this.communicationBroker.findService(id);
    }

    public List<MSData> getServicesByType(EServiceType type) {
        return this.communicationBroker.getServicesByType(type);
    }

}
