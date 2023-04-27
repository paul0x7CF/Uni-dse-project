package msExchange.networkCommunication;

import network.InputSocket;
import network.NetworkHandler;
import network.OutputSocket;
import org.apache.logging.log4j.message.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;


public class MessageHandler {
    private int port;
    private InputSocket inputSocket;
    private OutputSocket outputSocket;
    private NetworkHandler network = new NetworkHandler();
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    private final int MAX_AUCTIONS= 200;
    public MessageHandler(int port) {
    }
    //incoming messages
    public void handleMessage(Message message){}

    private void updateTimeSlots(List<TimeSlot> timeslots){}
    private void receivedBid(Bid bid){}
    private void receivedSell(Sell sell){}

    //outgoing messages
    private void register(){}//von Konstruktor aufgerufen
    public void sendTransactions(List<Transaction> transactions){}
    public void sendAuctionInformation(Map<UUID, Double> auctionInformation){}
    private void tellCapacityReached(){}

}
