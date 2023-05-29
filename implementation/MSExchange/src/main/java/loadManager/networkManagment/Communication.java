package loadManager.networkManagment;

import broker.BrokerRunner;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class Communication {
    private static final Logger logger = Logger.getLogger(Communication.class.getName());
    private final int PORT;
    private final String ADDRESS;
    private final EServiceType SERVICE_TYPE;
    private MSData myMSData;
    private BrokerRunner communicationBroker;
    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;

    public Communication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        //read Properties
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("src/main/java/config.properties");
            properties.load(configFile);
            configFile.close();

            PORT = Integer.parseInt(properties.getProperty("loadManager.port"));
            ADDRESS = properties.getProperty("loadManager.ip");
            SERVICE_TYPE = EServiceType.valueOf(properties.getProperty("loadManager.serviceType"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        createBroker(PORT);

        logger.info("MS registered with Id:" + this.myMSData.getId() + ", Address: " + this.myMSData.getAddress() + ", Port: " + this.myMSData.getPort());
    }


    private void createBroker(int port) {
        this.communicationBroker = new BrokerRunner(EServiceType.Exchange, port);
        this.myMSData = this.communicationBroker.getCurrentService();
    }

    public void startBrokerRunner() {
        this.communicationBroker.run();
    }

    private void sendMessage(Message message) {
        communicationBroker.sendMessage(message);
    }
}