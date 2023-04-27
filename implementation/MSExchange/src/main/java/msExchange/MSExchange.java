package msExchange;

import msExchange.networkCommunication.MessageHandler;
import msExchange.timeSlotManagement.TimeSlotManager;
import org.apache.logging.log4j.message.Message;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class MSExchange {
    private UUID exchangeID;
    private boolean duplicated;
    private TimeSlotManager timeSlotManager;
    private MessageHandler messageHandler;

    private BlockingQueue<Message> messageQueue;

    public MSExchange(boolean duplicated) {
    }

    public void start() {
    }

}
