package msExchange;

import msExchange.networkCommunication.MessageHandler;
import msExchange.timeSlotManagement.TimeSlotManager;

import java.util.UUID;

public class MSExchange  {
    private TimeSlotManager timeSlotManager;
    private MessageHandler messageHandler;
    private UUID exchangeID;
    public void start() {
    }

    public TimeSlotManager getTimeSlotManager(){return timeSlotManager;}


}
