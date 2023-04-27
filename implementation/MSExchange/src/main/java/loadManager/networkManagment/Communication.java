package loadManager.networkManagment;

import loadManager.Controller;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import message.Message;
import network.InputSocket;
import network.OutputSocket;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Communication {
    private int port;
    private String host;
    private Controller controller;

    private network.NetworkHandler network;
    private Broker broker;
    private InputSocket inputSocket;
    private OutputSocket outputSocket;

    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;

    public Communication(int port, String host, Controller controller) throws IOException {
        this.port = port;
        this.host = host;

        this.inputSocket= new InputSocket(port, host);
        this.outputSocket = new OutputSocket(port, host);
        this.network = new network.NetworkHandler(inputSocket);
        this.controller = controller;
    }

    //incoming messages
    public void handleIncomingMessage(Message message) {
    }

    private void handleIncomingBid(Bid bid) {
    }

    private void handleIncomingSell(Sell sell) {
    }

    private void handleNewExchangeService(Message message) {
    }

    private void handleExchangeServiceAtCapacity(Message message) {
    }

    private void handleDuplicateMicroservice() {
    }

    private void handleIncomingAuctions(Message message) {
    }

    private void handleIncomingTransaction(Transaction transaction) {
    }

    //outgoing messages
    public void connectToExchange(UUID exchangeUUID) {
    }

    public void tellNewTimeslots(List<TimeSlot> timeslots) {
    }

    public void deleteExchange(ExchangeServiceInformation exchangeServiceInformation) {
    }

    public void tellProsumerPriceNotOK(UUID prosumerId, double averagePrice) {
    }

    public void askAboutAuctions() {
    }

    public void sendProsumerToStorage(UUID prosumerId, double kwh) {
    }

    private void askStoragesAboutCapacity() {
    }
}
