package MSS.communication;

import CF.broker.Broker;
import MSS.storage.MSStorageManager;
import CF.protocol.Message;
import CF.sendable.MSData;
import CF.sendable.Transaction;

import java.util.concurrent.BlockingQueue;

public class Communication {
    private String ipAddress;
    private int port;
    private Broker communicationBroker; // TODO Günther: Don't instantiate broker here, use BrokerRunner
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
