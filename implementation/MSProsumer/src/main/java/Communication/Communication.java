package Communication;

import Logic.AuctionMessageHandler;
import Logic.Prosumer;
import MSProsumer.Main.ProsumerManager;
import broker.BrokerRunner;
import messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;
import sendable.TimeSlot;

import java.util.concurrent.BlockingQueue;

public class Communication {

    private static final Logger logger = LogManager.getLogger(Communication.class);
    private int port;
    private String ipAdress;

    private MSData myMSData;

    private BrokerRunner communicationBroker;

    private BlockingQueue<TimeSlot> inputQueueTimeSlot;

    private BlockingQueue<Message> incomingMessages;

    private BlockingQueue<Message> outgoingMessages;

    private ProsumerManager prosumerManager;

    private MessageHandler prosMessageHandler;

    public Communication(BlockingQueue<TimeSlot> inputQueueTimeSlot, BlockingQueue<Message> inputForecastResponse, BlockingQueue<Message> outgoingMessages, ProsumerManager prosumerManager) {
        this.inputQueueTimeSlot = inputQueueTimeSlot;
        //this.inputForecastResponse = inputForecastResponse;
        this.outgoingMessages = outgoingMessages;
        this.prosumerManager = prosumerManager;
    }

    public Communication(BlockingQueue<Message> incomingMessages, BlockingQueue<Message> outgoingMessages, final int port) {
        this.incomingMessages = outgoingMessages;
        this.outgoingMessages = outgoingMessages;
        createBroker(port);

        logger.info("MS registered with Id: {} Ip: {} Port: {}", this.myMSData.getId(), this.myMSData.getAddress(), this.myMSData.getPort());
    }

    private void createBroker(final int port) {
        this.communicationBroker = new BrokerRunner(EServiceType.Prosumer, port);
        this.myMSData = this.communicationBroker.getCurrentService();
        new Thread(communicationBroker).start();
    }

    private void sendMessage(Message message) {

        //communicationBroker.addMessageHandler(ECategory.Auction, new AuctionMessageHandler(communicationBroker, prosumer));


    }



}
