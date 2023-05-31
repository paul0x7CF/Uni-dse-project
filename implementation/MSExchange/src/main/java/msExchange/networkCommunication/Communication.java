package msExchange.networkCommunication;

import broker.BrokerRunner;
import broker.IServiceBroker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;


public class Communication{
    private static final Logger logger = LogManager.getLogger(Communication.class.getName());
    private MSData myMSData;
    private BrokerRunner communicationBroker;
    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;

    public Communication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages) {
        //read Properties
        Properties properties = new Properties();
        int port;
        EServiceType serviceType;
        try {
            FileInputStream configFile = new FileInputStream("C:\\Universität\\DSE\\Gruppenprojekt\\DSE_Team_202\\implementation\\MSExchange\\src\\main\\java\\config.properties");
            properties.load(configFile);
            configFile.close();

            //TODO: Where do I get the port/address?
            port = Integer.parseInt(properties.getProperty("exchange.port"));
            serviceType = EServiceType.valueOf(properties.getProperty("exchange.serviceType"));
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

    public void addMessageHandler(ECategory category) {
        try {
            switch (category) {
                case Auction -> {
                    this.communicationBroker.addMessageHandler(ECategory.Auction, new ProsumerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker));
                }
                case Exchange -> {
                    this.communicationBroker.addMessageHandler(ECategory.Exchange, new LoadBalancerMessageHandler(this.incomingMessages, this.outgoingMessages, (IServiceBroker) this.communicationBroker));
                }
            }
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
