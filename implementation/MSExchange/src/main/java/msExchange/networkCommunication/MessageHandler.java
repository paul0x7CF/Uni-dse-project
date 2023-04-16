package msExchange.networkCommunication;

import message.Message;
import network.NetworkHandler;
import network.ReceiveSocket;
import network.SendSocket;
import sendable.Bid;
import sendable.Sell;
import sendable.Timeslot;
import sendable.Transaction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class MessageHandler {
    private int port;
    private ReceiveSocket receiveSocket;
    private SendSocket sendSocket;
    private NetworkHandler network;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    private final int MAX_AUCTIONS= 200;
    public MessageHandler(int port) {
    }
    //incoming messages
    public void handleMessage(Message message){}

    private void updateTimeSlots(List<Timeslot> timeslots){}
    private void reiceivedBid(Bid bid){}
    private void receivedSell(Sell sell){}

    //outgoing messages
    private void register(){}//von Konstruktor aufgerufen
    public void sendTransactions(List<Transaction> transactions){}
    public void sendAuctionInformation(Map<UUID, Double> auctionInformation){}
    public void capacityReached(){}

}
