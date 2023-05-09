package communication;

import MSStorage.main.MSStorageManager;
import broker.Broker;
import protocol.Message;
import sendable.MSData;
import sendable.Transaction;

import java.util.concurrent.BlockingQueue;

public class Communication {
    private String ipAddress;
    private int port;
    private Broker communicationBroker;
    private BlockingQueue<Transaction> incomingTransactionQueue;
    private MSData msData;
    private MSStorageManager msStorageManager;

    public Communication(BlockingQueue<Transaction> incomingTransactionQueue) {
        this.incomingTransactionQueue = incomingTransactionQueue;
    }

    public void stopMS() {

    }

    public void handleExchange(Message message) {

    }

    public void sendMessage(Message message) {

    }
}
