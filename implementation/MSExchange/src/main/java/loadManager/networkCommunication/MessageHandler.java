package loadManager.networkCommunication;

import loadManager.ExchangeServiceInformation;
import message.Message;
import network.NetworkHandler;
import network.ReceiveSocket;
import network.SendSocket;
import sendable.Bid;
import sendable.Sell;
import sendable.Timeslot;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessageHandler {
    private int port;
    private NetworkHandler network;
    private ReceiveSocket receiveSocket;
    private SendSocket sendSocket;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;

    public MessageHandler() {
        network= new NetworkHandler();
    }

    //incoming messages
    public void handleMessage(Message message){}
    private void reiceivedBid(Bid bid){}
    private void receivedSell(Sell sell){}


    //outgoing messages
    private void tellNewTimeslots(List<Timeslot> timeslots){}
    private void askExchangesForCapacity(){}
    private boolean isExchangeAtCapacity(ExchangeServiceInformation exchange){return false;}


}
