package msExchange;

import msExchange.networkCommunication.MessageHandler;
import sendable.Sell;
import sendable.Timeslot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MSExchange {
    private UUID exchangeID;
    private List<Timeslot> timeSlots = new ArrayList<>();
    public MessageHandler messageHandler;

    public void start(){}
    public void setTimeSlots(List<Timeslot> timeSlots){}
    public void addAuctionToSlot(UUID timeSlotId, Sell sellPosition){}




}
