package MSProsumer.Logic;

import protocol.Message;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class DemandManager implements Runnable{

    private UUID timeSlotID;
    private BlockingQueue<Message> responseMessages;
    private BlockingQueue<Message> outgoingMessages;

    public DemandManager(UUID timeSlotID, BlockingQueue<Message> responseMessages, BlockingQueue<Message> outgoingMessages) {
        this.timeSlotID = timeSlotID;
        this.responseMessages = responseMessages;
        this.outgoingMessages = outgoingMessages;
    }
    @Override
    public void run() {

    }
}
