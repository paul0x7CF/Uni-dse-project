package MSProsumer.Main;

import Communication.Communication;
import Logic.Prosumer;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager {
    private HashMap<UUID, Prosumer> prosumers;
    private HashMap<UUID, BlockingQueue<TimeSlot>> prosumerTimeSlots;
    private BlockingQueue<Message> outgoingMessages;
    private BlockingQueue<TimeSlot> inputQueueTimeSlots;
    private BlockingQueue<Message> inputForecastResponse;
    private Communication communicator;

    public ProsumerManager() {
        this.communicator = new Communication(this.inputQueueTimeSlots, this.inputForecastResponse, this.outgoingMessages, this);
    }

    public void createProsumer() {

    }

    private void addAvailableTimeSlotToProsumers(TimeSlot timeSlot) {

    }

    public void handleSellLowerQuestion(Sell sell) {

    }

    public void handleBidHigherQuestion(Bid bid) {

    }

    public void handleForecastResponse(Message message) {

    }

    public void handleTransactionResponse(Transaction transaction) {

    }

    private Prosumer getProsumerByID(UUID id) {
        return null;
    }



}
