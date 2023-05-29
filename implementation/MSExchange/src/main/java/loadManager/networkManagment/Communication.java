package loadManager.networkManagment;

import broker.BrokerRunner;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class Communication {
    private static final Logger logger = Logger.getLogger(Communication.class.getName());
    private int port;
    private String ipAdress;

    private MSData myMSData;

    private BrokerRunner communicationBroker;

    private BlockingQueue<Message> incomingMessages;
    private BlockingQueue<Message> outgoingMessages;

    public Communication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, int port, String ipAdress) {
        this.incomingMessages = incomingMessages;
        this.outgoingMessages = outgoingMessages;
        this.port = port;
        this.ipAdress = ipAdress;
        createBroker(port);

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