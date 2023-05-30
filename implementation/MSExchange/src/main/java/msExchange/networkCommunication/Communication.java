package msExchange.networkCommunication;

import broker.BrokerRunner;
import broker.IServiceBroker;
import loadManager.networkManagment.AuctionMessageHandler;
import loadManager.networkManagment.ExchangeMessageHandler;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class Communication {
    private static final Logger logger = Logger.getLogger(Communication.class.getName());
    private final int PORT;
    private final String ADDRESS;
    private final EServiceType SERVICE_TYPE;
    private final UUID ID;
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

            //TODO: Where do I get the port/address?
            PORT = Integer.parseInt(properties.getProperty("exchange.port"));
            ADDRESS = properties.getProperty("exchange.ip");
            SERVICE_TYPE = EServiceType.valueOf(properties.getProperty("exchange.serviceType"));
            ID = UUID.randomUUID();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        createBroker(PORT);
        createMYMSData();

        logger.info("MS registered with Id:" + this.myMSData.getId() + ", Address: " + this.myMSData.getAddress() + ", Port: " + this.myMSData.getPort());
    }

    private void createMYMSData() {
        this.myMSData = new MSData(ID, SERVICE_TYPE, ADDRESS, PORT);
    }


    private void createBroker(int port) {
        this.communicationBroker = new BrokerRunner(SERVICE_TYPE, port);
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

}
