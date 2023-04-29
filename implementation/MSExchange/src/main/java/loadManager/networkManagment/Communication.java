/*package loadManager.networkManagment;

import broker.Broker;
import loadManager.Controller;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import communication.InputSocket;
import communication.OutputSocket;
import protocol.Message;
import sendable.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Communication {
    private int port;
    private String host;
    private Controller controller;
    private communication.NetworkHandler network;
    private Broker broker; //TODO: Just call broker.sendMessage(Message) instead of having the sockets.
    private InputSocket inputSocket; //TODO: Why is this here? Broker has one already
    private OutputSocket outputSocket; //TODO: Why is this here? Broker has one already

    private BlockingQueue<Message> incomingQueue;
    private BlockingQueue<Message> outgoingQueue;

    public Communication(int port, String host, Controller controller) {
        this.port = port;
        this.host = host;

        // TODO this.inputSocket= new InputSocket(port, host);
        // TODO this.outputSocket = new OutputSocket(port, host);
        this.network = new communication.NetworkHandler(EServiceType.Exchange, port);
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
*/