package loadManager;

import broker.Broker;
import broker.BrokerRunner;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import loadManager.prosumerActionManagement.ProsumerManager;
import loadManager.timeSlotManagement.TimeSlotManager;
import messageHandling.IMessageHandler;
import protocol.ECategory;
import protocol.Message;
import sendable.*;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class Controller implements Runnable {
    private final int PORT = 1608;
    private final String ADDRESS = "10.102.102.13";
    private final EServiceType SERVICE_TYPE = EServiceType.Exchange;
    private ProsumerManager prosumerManager;
    private BrokerRunner broker;
    private TimeSlotManager timeSlotManager = new TimeSlotManager();
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    private LoadManager loadManager = new LoadManager();
    private ExecutorService executorService;
    private IMessageHandler messageHandler;
    public Controller() {
        broker = new BrokerRunner(SERVICE_TYPE, PORT);
        broker.run();
        broker.addMessageHandler(ECategory.Exchange, messageHandler);
    }


    @Override
    public void run() {

    }

    private void processIncomingQueue() {
    }

    private void processOutgoingQueue() {
    }

    private void handleBid(Bid bid) {
    }

    private void handleSell(Sell sell, UUID exchangeID) {
    }

    private void handleExchangeService(ExchangeServiceInformation exchangeServiceInformation) {
    }

    private void handleIncomingTransaction(Transaction transaction) {
    }
}
