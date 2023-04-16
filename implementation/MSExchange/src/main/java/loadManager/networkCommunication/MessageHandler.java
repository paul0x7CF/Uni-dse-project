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
    private InputSocket inputSocket;
    private OutputSocket outputSocket;
    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;
    public MessageHandler() {
        network= new NetworkHandler();
    }

    //incoming messages
    public void handleMessage(Message message){}
    private void receivedBid(Bid bid){}
    private void receivedSell(Sell sell){}
    private void recordNewExchangeService(Message message){}
    private void receivedInformationExchangeAtCapacity(Message message){}
    private void duplicateMicroservice(){}

    //outgoing messages
    private void tellNewTimeslots(List<Timeslot> timeslots){}
    private void deleteExchange(ExchangeServiceInformation exchangeServiceInformation){}



}
