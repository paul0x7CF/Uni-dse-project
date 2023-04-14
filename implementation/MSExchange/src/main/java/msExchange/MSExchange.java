package msExchange;

import message.Message;
import msExchange.networkCommunication.MessageHandler;
import msExchange.timeSlotManagement.TimeSlotManager;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class MSExchange  {
    private TimeSlotManager timeSlotManager;
    private MessageHandler messageHandler;
    private UUID exchangeID;
    private BlockingQueue<Message> messageQueue;
    public void start() {
    }

    public TimeSlotManager getTimeSlotManager(){return timeSlotManager;}

}
