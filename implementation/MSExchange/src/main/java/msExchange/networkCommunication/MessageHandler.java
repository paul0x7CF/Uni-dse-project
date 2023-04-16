package msExchange.networkCommunication;

import message.Message;
import network.InputSocket;
import network.NetworkHandler;
import network.OutputSocket;

public class MessageHandler {
    private int port;
    private InputSocket inputSocket;
    private OutputSocket outputSocket;
    private NetworkHandler network;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    private final int MAX_AUCTIONS= 200;
    public MessageHandler(int port) {
    }
    //incoming messages
    public void handleMessage(Message message){}

    private void updateTimeSlots(List<TimeSlot> timeslots){}
    private void reiceivedBid(Bid bid){}
    private void receivedSell(Sell sell){}

    //outgoing messages
    private void register(){}//von Konstruktor aufgerufen
    public void sendTransactions(List<Transaction> transactions){}
    public void sendAuctionInformation(Map<UUID, Double> auctionInformation){}
    private void tellCapacityReached(){}

}
