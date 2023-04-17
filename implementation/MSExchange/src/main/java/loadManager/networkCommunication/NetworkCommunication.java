package loadManager.networkCommunication;

import loadManager.ExchangeServiceInformation;
import loadManager.LoadManager;
import message.Message;
import message.MessageBuilder;
import network.InputSocket;
import network.OutputSocket;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class NetworkCommunication {
    private int port;
    private LoadManager loadManager;
    private InputSocket inputSocket;
    private OutputSocket outputSocket;
    private network.NetworkHandler network;
    private MessageBuilder messageBuilder;

    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;

    public NetworkCommunication(LoadManager loadManager, int port) {
        this.port = port;
        this.network= new network.NetworkHandler();
        this.loadManager= loadManager;
    }

    //incoming messages
    public void handleIncomingMessage(Message message){}
    private void handleIncomingBid(Bid bid){}
    private void handleIncomingSell(Sell sell){}
    private void handleNewExchangeService(Message message){}
    private void handleExchangeServiceAtCapacity(Message message){}
    private void handleDuplicateMicroservice(){}
    private void handleIncomingAuctions(Message message){}
    private void handleIncomingTransaction(Transaction transaction){}

    //outgoing messages
    public void connectToExchange(UUID exchangeUUID){}
    public void tellNewTimeslots(List<TimeSlot> timeslots){}
    public void deleteExchange(ExchangeServiceInformation exchangeServiceInformation){}
    public void tellProsumerPriceNotOK(UUID prosumerId, double averagePrice){}
    public void askAboutAuctions(){}

    public void sendProsumerToStorage(UUID prosumerId, double kwh){}
    private void askStoragesAboutCapacity(){}
}
