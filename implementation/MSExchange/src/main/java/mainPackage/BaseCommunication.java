package mainPackage;

import broker.BrokerRunner;
import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

public abstract class BaseCommunication {
    private static final Logger logger = LogManager.getLogger(BaseCommunication.class.getName());
    private MSData myMSData;
    protected BrokerRunner communicationBroker;
    protected BlockingQueue<Message> incomingMessages;
    protected BlockingQueue<Message> outgoingMessages;


    public BaseCommunication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages,
                             String propertiesFilePath, EExchangeType exchangeType) {
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
                case Exchange -> {
                    port = Integer.parseInt(properties.getProperty("exchange.port"));
                    serviceType = EServiceType.valueOf(properties.getProperty("exchange.serviceType"));
                }
                case LoadManager -> {
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

        logger.info("MS registered with Id:" + this.myMSData.getId() + ", Address: " + this.myMSData.getAddress() + ", Port: " + this.myMSData.getPort());
    }

    private void createBroker(int port, EServiceType serviceType) {
        this.communicationBroker = new BrokerRunner(serviceType, port);
        this.myMSData = this.communicationBroker.getCurrentService();
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
        return myMSData;
    }

}
