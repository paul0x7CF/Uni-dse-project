package MSP.Main;

import MSP.Communication.Communication;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import CF.protocol.Message;
import CF.sendable.Bid;
import CF.sendable.Sell;
import CF.sendable.TimeSlot;
import CF.sendable.Transaction;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProsumerManager {
    private HashMap<UUID, ConsumptionBuilding> prosumers;
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

    private ConsumptionBuilding getProsumerByID(UUID id) {
        return null;
    }



}
